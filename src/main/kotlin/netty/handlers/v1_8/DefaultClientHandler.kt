package dev.spaghett.netty.handlers.v1_8

import dev.spaghett.netty.STATE_KEY
import dev.spaghett.netty.codec.AesDecoder
import dev.spaghett.netty.codec.AesEncoder
import dev.spaghett.netty.codec.FramingDecoder
import dev.spaghett.netty.codec.FramingEncoder
import dev.spaghett.netty.handlers.PacketHandler
import dev.spaghett.netty.instance.ClientConfiguration
import dev.spaghett.packet.Packet
import dev.spaghett.packet.ProtocolState
import dev.spaghett.protocol.v1_8.handshake.client.C00Handshake
import dev.spaghett.protocol.v1_8.login.client.C00LoginStart
import dev.spaghett.protocol.v1_8.login.client.C01EncryptionResponse
import dev.spaghett.protocol.v1_8.login.server.S00Disconnect
import dev.spaghett.protocol.v1_8.login.server.S01EncryptionRequest
import dev.spaghett.protocol.v1_8.login.server.S02LoginSuccess
import dev.spaghett.protocol.v1_8.login.server.S03SetCompression
import dev.spaghett.protocol.v1_8.status.client.C00StatusRequest
import dev.spaghett.protocol.v1_8.status.client.C01StatusPing
import dev.spaghett.protocol.v1_8.status.server.S00StatusResponse
import dev.spaghett.utils.CryptoUtil
import dev.spaghett.utils.SessionServerUtil
import io.netty.channel.ChannelHandlerContext
import java.security.KeyFactory
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

open class DefaultClientHandler(private val config: ClientConfiguration) : PacketHandler() {
    override fun handshake(ctx: ChannelHandlerContext, packet: Packet) {
        // There are no clientbound handshake packets
    }

    override fun status(ctx: ChannelHandlerContext, packet: Packet) {
        when (packet) {
            is S00StatusResponse -> {
                logger.info("Received status response: ${packet.json}")

                val pingPacket = C01StatusPing().apply {
                    payload = System.currentTimeMillis()
                }
                ctx.writeAndFlush(pingPacket)
            }

            is C01StatusPing -> {
                val pingTime = packet.payload
                logger.info("Received ping response: $pingTime")
            }
        }
    }

    override fun login(ctx: ChannelHandlerContext, packet: Packet) {
        when (packet) {
            is S00Disconnect -> {
                logger.error("Disconnected from server: ${packet.reason}")
                ctx.close()
            }

            is S01EncryptionRequest -> {
                logger.info("Received encryption request from server.")

                val session = config.session ?: run {
                    logger.error("Minecraft Java session is null, cannot proceed with encryption.")
                    return
                }

                val keySpec = X509EncodedKeySpec(packet.publicKey)
                val keyFactory = KeyFactory.getInstance("RSA")
                val serverPub = keyFactory.generatePublic(keySpec)

                val sharedSecret = ByteArray(16).also { SecureRandom().nextBytes(it) }

                val serverIdHash = CryptoUtil.digestServerId(
                    serverId = "",
                    publicKey = serverPub,
                    sharedSecret = sharedSecret
                )

                SessionServerUtil.joinSessionServer(
                    session.mcProfile.mcToken.accessToken,
                    session.mcProfile.id.toString(),
                    serverIdHash
                ) {
                    val rsa = Cipher.getInstance("RSA")
                    rsa.init(Cipher.ENCRYPT_MODE, serverPub)
                    val secretEnc = rsa.doFinal(sharedSecret)
                    val tokenEnc = rsa.doFinal(packet.verifyToken)

                    val resp = C01EncryptionResponse().apply {
                        sharedSecretLength = secretEnc.size
                        this.sharedSecret = secretEnc
                        verifyTokenLength = tokenEnc.size
                        verifyToken = tokenEnc
                    }
                    ctx.writeAndFlush(resp)

                    val ivSpec = IvParameterSpec(sharedSecret)
                    val encCipher = Cipher.getInstance("AES/CFB8/NoPadding").apply {
                        init(Cipher.ENCRYPT_MODE, SecretKeySpec(sharedSecret, "AES"), ivSpec)
                    }
                    val decCipher = Cipher.getInstance("AES/CFB8/NoPadding").apply {
                        init(Cipher.DECRYPT_MODE, SecretKeySpec(sharedSecret, "AES"), ivSpec)
                    }

                    val pl = ctx.pipeline()
                    pl.addBefore("framingDecoder", "aesDecoder", AesDecoder(decCipher))
                    pl.addBefore("framingEncoder", "aesEncoder", AesEncoder(encCipher))
                }
            }

            is S02LoginSuccess -> {
                logger.info("Login successful! Welcome, ${packet.username}!")
                ctx.channel().attr(STATE_KEY).set(ProtocolState.PLAY)
            }

            is S03SetCompression -> {
                logger.info("Compression set to ${packet.threshold} bytes.")

                if (packet.threshold > 0) {
                    val framingDecoder = ctx.channel().pipeline().get("framingDecoder") as FramingDecoder
                    framingDecoder.setThreshold(packet.threshold)
                    val framingEncoder = ctx.channel().pipeline().get("framingEncoder") as FramingEncoder
                    framingEncoder.setThreshold(packet.threshold)
                }
            }
        }
    }

    override fun play(ctx: ChannelHandlerContext, packet: Packet) {
        // don't do anything by default
    }

    override fun configuration(ctx: ChannelHandlerContext, packet: Packet) {
        // no configuration in 1.8
    }

    override fun onConnectionOpened(ctx: ChannelHandlerContext) {
        beginLogin(ctx)
    }

    private fun beginLogin(ctx: ChannelHandlerContext) {
        val handshake = C00Handshake().apply {
            protocolVersion = 47
            serverAddress = config.host
            serverPort = config.port.toUShort()
            nextState = 2
        }
        ctx.writeAndFlush(handshake)

        ctx.channel().attr(STATE_KEY).set(ProtocolState.LOGIN)

        val loginStart = C00LoginStart().apply {
            username = config.username
        }
        ctx.writeAndFlush(loginStart)
    }

    /**
     * Testing method for server list ping
     */
    private fun sendStatusRequest(ctx: ChannelHandlerContext) {
        val handshake = C00Handshake().apply {
            protocolVersion = 47
            serverAddress = config.host
            serverPort = config.port.toUShort()
            nextState = 1 // Status state
        }
        ctx.writeAndFlush(handshake)
        ctx.channel().attr(STATE_KEY).set(ProtocolState.STATUS)

        val packet = C00StatusRequest()
        ctx.writeAndFlush(packet)
    }
}