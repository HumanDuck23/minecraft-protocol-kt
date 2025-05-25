package dev.spaghett.netty.handlers

import dev.spaghett.netty.STATE_KEY
import dev.spaghett.netty.instance.ServerConfiguration
import dev.spaghett.packet.Packet
import dev.spaghett.packet.ProtocolState
import dev.spaghett.protocol.handshake.client.C00Handshake
import io.netty.channel.ChannelHandlerContext

abstract class ServerPacketHandler(private val config: ServerConfiguration) : PacketHandler() {
    override fun handshake(ctx: ChannelHandlerContext, packet: Packet) {
        val handshake = packet as C00Handshake

        val next = ProtocolState.fromId(handshake.nextState)
        ctx.channel().attr(STATE_KEY).set(next)
    }
}