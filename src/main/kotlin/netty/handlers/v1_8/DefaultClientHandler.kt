package dev.spaghett.netty.handlers.v1_8

import dev.spaghett.netty.STATE_KEY
import dev.spaghett.netty.codec.FramingDecoder
import dev.spaghett.netty.codec.FramingEncoder
import dev.spaghett.netty.handlers.PacketHandler
import dev.spaghett.netty.instance.ClientConfiguration
import dev.spaghett.packet.Packet
import dev.spaghett.packet.ProtocolState
import dev.spaghett.protocol.v1_8.handshake.client.C00Handshake
import dev.spaghett.protocol.v1_8.login.client.C00LoginStart
import dev.spaghett.protocol.v1_8.login.server.S00Disconnect
import dev.spaghett.protocol.v1_8.login.server.S01EncryptionRequest
import dev.spaghett.protocol.v1_8.login.server.S02LoginSuccess
import dev.spaghett.protocol.v1_8.login.server.S03SetCompression
import dev.spaghett.protocol.v1_8.status.client.C00StatusRequest
import dev.spaghett.protocol.v1_8.status.client.C01StatusPing
import dev.spaghett.protocol.v1_8.status.server.S00StatusResponse
import io.netty.channel.ChannelHandlerContext

class DefaultClientHandler(private val config: ClientConfiguration) : PacketHandler() {
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