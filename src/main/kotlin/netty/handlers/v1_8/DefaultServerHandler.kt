package dev.spaghett.netty.handlers.v1_8

import com.google.gson.JsonObject
import dev.spaghett.netty.GAME_PROFILE_KEY
import dev.spaghett.netty.STATE_KEY
import dev.spaghett.netty.VERIFY_TOKEN_KEY
import dev.spaghett.netty.codec.AesDecoder
import dev.spaghett.netty.codec.AesEncoder
import dev.spaghett.netty.codec.FramingDecoder
import dev.spaghett.netty.codec.FramingEncoder
import dev.spaghett.netty.handlers.PacketHandler
import dev.spaghett.netty.instance.ServerConfiguration
import dev.spaghett.packet.Packet
import dev.spaghett.packet.ProtocolState
import dev.spaghett.protocol.GameProfile
import dev.spaghett.protocol.v1_8.handshake.client.C00Handshake
import dev.spaghett.protocol.v1_8.login.client.C00LoginStart
import dev.spaghett.protocol.v1_8.login.client.C01EncryptionResponse
import dev.spaghett.protocol.v1_8.login.server.S01EncryptionRequest
import dev.spaghett.protocol.v1_8.login.server.S02LoginSuccess
import dev.spaghett.protocol.v1_8.login.server.S03SetCompression
import dev.spaghett.protocol.v1_8.play.client.C17PluginMessage
import dev.spaghett.protocol.v1_8.play.server.*
import dev.spaghett.protocol.v1_8.status.client.C00StatusRequest
import dev.spaghett.protocol.v1_8.status.client.C01StatusPing
import dev.spaghett.protocol.v1_8.status.server.S00StatusResponse
import dev.spaghett.protocol.v1_8.status.server.S01StatusPong
import dev.spaghett.utils.CryptoUtil
import dev.spaghett.utils.SessionServerUtil
import io.netty.channel.ChannelHandlerContext
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class DefaultServerHandler(private val config: ServerConfiguration) : PacketHandler() {
    override fun handshake(ctx: ChannelHandlerContext, packet: Packet) {
        val handshake = packet as C00Handshake

        val next = ProtocolState.fromId(handshake.nextState)
        ctx.channel().attr(STATE_KEY).set(next)
    }

    override fun status(ctx: ChannelHandlerContext, packet: Packet) {
        when (packet) {
            is C00StatusRequest -> {
                val json = JsonObject()

                val version = JsonObject().apply {
                    addProperty("name", "1.8.9")
                    addProperty("protocol",  47)
                }

                val players = JsonObject().apply {
                    addProperty("max", config.maxPlayers)
                    addProperty("online", 0)
                }

                val description = JsonObject().apply {
                    addProperty("text", config.motd)
                }

                json.apply {
                    add("version", version)
                    add("players", players)
                    add("description", description)
                }

                val response = S00StatusResponse()
                response.json = json.toString()
                ctx.writeAndFlush(response)
            }

            is C01StatusPing -> {
                val pingTime = packet.payload

                val pongResponse = S01StatusPong()
                pongResponse.payload = pingTime
                ctx.writeAndFlush(pongResponse)

                ctx.close() // Close the connection after sending the pong response
            }

            else -> {
                logger.warn("Unknown status packet received: {}", packet.meta)
            }
        }
    }

    override fun login(ctx: ChannelHandlerContext, packet: Packet) {
        when (packet) {
            is C00LoginStart -> {
                if (!config.onlineMode) {
                    ctx.channel().attr(GAME_PROFILE_KEY).set(GameProfile(UUID.randomUUID(), packet.username))
                    finishLogin(ctx)
                } else {
                    val token = CryptoUtil.newVerifyToken()
                    ctx.channel().attr(VERIFY_TOKEN_KEY).set(token)

                    val request = S01EncryptionRequest().apply {
                        serverID = ""
                        publicKey = CryptoUtil.keyPair.public.encoded
                        publicKeyLength = publicKey.size
                        verifyToken = token
                        verifyTokenLength = verifyToken.size
                    }

                    ctx.writeAndFlush(request)

                    ctx.channel().attr(GAME_PROFILE_KEY).set(GameProfile(UUID.randomUUID(), packet.username)) // Set a temporary profile
                }
            }
            is C01EncryptionResponse -> {
                val expectedToken = ctx.channel().attr(VERIFY_TOKEN_KEY).get()!!
                val receivedToken = CryptoUtil.rsaDecrypt(packet.verifyToken)

                require(receivedToken.contentEquals(expectedToken)) {
                    "Invalid verify token from client!"
                }

                val sharedSecret = CryptoUtil.rsaDecrypt(packet.sharedSecret)
                val ivSpec = IvParameterSpec(sharedSecret)
                val encCipher = Cipher.getInstance("AES/CFB8/NoPadding").apply {
                    init(Cipher.ENCRYPT_MODE, SecretKeySpec(sharedSecret, "AES"), ivSpec)
                }
                val decCipher = Cipher.getInstance("AES/CFB8/NoPadding").apply {
                    init(Cipher.DECRYPT_MODE, SecretKeySpec(sharedSecret, "AES"), ivSpec)
                }

                val pipeline = ctx.pipeline()
                pipeline.addBefore("framingDecoder", "aesDecoder", AesDecoder(decCipher))
                pipeline.addBefore("framingEncoder", "aesEncoder", AesEncoder(encCipher))

                val serverIdHash = CryptoUtil.digestServerId(
                    serverId   = "",
                    publicKey  = CryptoUtil.keyPair.public,
                    sharedSecret = sharedSecret
                )

                val tmpProfile = ctx.channel().attr(GAME_PROFILE_KEY).get()
                    ?: throw IllegalStateException("Game profile not set before encryption response")

                SessionServerUtil.verifyWithSessionServerAsync(tmpProfile.name, serverIdHash, ctx) { profile ->
                    ctx.channel().attr(GAME_PROFILE_KEY).set(profile)

                    finishLogin(ctx)
                }
            }
        }
    }

    override fun play(ctx: ChannelHandlerContext, packet: Packet) {
        when (packet) {
            is C17PluginMessage -> {
                if (packet.channel == "MC|Brand") {
                    val playerPositionAndLook = S08PlayerPositionAndLook().apply {
                        x = 0.0
                        y = 64.0
                        z = 0.0
                        yaw = 0f
                        pitch = 0f
                        flags = 0
                    }
                    ctx.writeAndFlush(playerPositionAndLook)
                }
            }
        }
    }

    override fun configuration(ctx: ChannelHandlerContext, packet: Packet) {
        // do nothing this doesn't exist in 1.8
    }

    private fun finishLogin(ctx: ChannelHandlerContext) {
        if (config.compressionThreshold > 0) {
            val compressionPacket = S03SetCompression().apply {
                threshold = config.compressionThreshold
            }
            ctx.writeAndFlush(compressionPacket)

            val framingDecoder = ctx.channel().pipeline().get("framingDecoder") as FramingDecoder
            framingDecoder.setThreshold(config.compressionThreshold)
            val framingEncoder = ctx.channel().pipeline().get("framingEncoder") as FramingEncoder
            framingEncoder.setThreshold(config.compressionThreshold)
        }

        val profile = ctx.channel().attr(GAME_PROFILE_KEY).get() ?: throw IllegalStateException("Game profile not set")

        val success = S02LoginSuccess()
        success.uuid = profile.id.toString()
        success.username = profile.name
        ctx.writeAndFlush(success)

        ctx.channel().attr(STATE_KEY).set(ProtocolState.PLAY)

        val joinGame = S01JoinGame().apply {
            entityId = 1
            gamemode = 0
            dimension = 0
            difficulty = 1
            maxPlayers = 20
            levelType = "default"
            reducedDebugInfo = false
        }
        ctx.writeAndFlush(joinGame)

        val brand = S3FPluginMessage().apply {
            channel = "MC|Brand"
            data = "Spaghetto".toByteArray()
        }
        ctx.writeAndFlush(brand)

        val serverDifficulty = S41ServerDifficulty().apply {
            difficulty = 1
        }
        ctx.writeAndFlush(serverDifficulty)

        val spawnPosition = S05SpawnPosition().apply {
            location = Triple(0, 64, 0)
        }
        ctx.writeAndFlush(spawnPosition)

        val playerAbilities = S39PlayerAbilities().apply {
            flags = 0x01
            flyingSpeed = 1f
            fovModifier = 1f
        }
        ctx.writeAndFlush(playerAbilities)
    }
}