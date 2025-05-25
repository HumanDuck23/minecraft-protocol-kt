package dev.spaghett.netty.handlers

import com.google.gson.JsonObject
import dev.spaghett.netty.STATE_KEY
import dev.spaghett.netty.instance.ServerConfiguration
import dev.spaghett.packet.Packet
import dev.spaghett.packet.ProtocolState
import dev.spaghett.protocol.handshake.client.C00Handshake
import dev.spaghett.protocol.login.client.C00LoginStart
import dev.spaghett.protocol.login.server.S02LoginSuccess
import dev.spaghett.protocol.play.client.C17PluginMessage
import dev.spaghett.protocol.play.server.*
import dev.spaghett.protocol.status.client.C00StatusRequest
import dev.spaghett.protocol.status.client.C01StatusPing
import dev.spaghett.protocol.status.server.S00StatusResponse
import io.netty.channel.ChannelHandlerContext
import java.util.UUID

class ServerHandler(private val config: ServerConfiguration) : PacketHandler() {
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
                println("Received ping: $pingTime ms")

                val pongResponse = dev.spaghett.protocol.status.server.S01StatusPong()
                pongResponse.payload = pingTime
                ctx.writeAndFlush(pongResponse)

                println("Sent pong!")
                ctx.close() // Close the connection after sending the pong response
            }

            else -> {
                println("Unknown status packet: ${packet.meta}")
            }
        }
    }

    override fun login(ctx: ChannelHandlerContext, packet: Packet) {
        when (packet) {
            is C00LoginStart -> {
                val success = S02LoginSuccess()
                success.uuid = UUID.nameUUIDFromBytes(packet.username.toByteArray()).toString()
                success.username = packet.username
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
    }

    override fun play(ctx: ChannelHandlerContext, packet: Packet) {
        when (packet) {
            is C17PluginMessage -> {
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