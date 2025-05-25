package dev.spaghett.netty.handlers

import com.google.gson.JsonObject
import dev.spaghett.netty.STATE_KEY
import dev.spaghett.packet.Packet
import dev.spaghett.packet.ProtocolState
import dev.spaghett.protocol.handshake.client.C00Handshake
import dev.spaghett.protocol.status.client.C00StatusRequest
import dev.spaghett.protocol.status.client.C01StatusPing
import dev.spaghett.protocol.status.server.S00StatusResponse
import io.netty.channel.ChannelHandlerContext

class ServerHandler : PacketHandler() {
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
                    addProperty("name", "Server")
                    addProperty("protocol",  47)
                }

                val players = JsonObject().apply {
                    addProperty("max", 20)
                    addProperty("online", 0)
                    add("sample", JsonObject().apply {
                        addProperty("name", "TestPlayer")
                        addProperty("id", "123e4567-e89b-12d3-a456-426614174000")
                    })
                }

                val description = JsonObject().apply {
                    addProperty("text", "Spaghetto's Minecraft Server")
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
        TODO("Not yet implemented")
    }

    override fun play(ctx: ChannelHandlerContext, packet: Packet) {
        TODO("Not yet implemented")
    }
}