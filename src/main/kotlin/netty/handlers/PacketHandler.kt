package dev.spaghett.netty.handlers

import dev.spaghett.netty.STATE_KEY
import dev.spaghett.packet.Packet
import dev.spaghett.packet.ProtocolState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

abstract class PacketHandler : SimpleChannelInboundHandler<Packet>() {
    override fun channelRead0(ctx: ChannelHandlerContext, packet: Packet) {
        val state = ctx.channel().attr(STATE_KEY).get()!!
        when (state) {
            ProtocolState.HANDSHAKE -> handshake(ctx, packet)
            ProtocolState.STATUS -> status(ctx, packet)
            ProtocolState.LOGIN -> login(ctx, packet)
            ProtocolState.PLAY -> play(ctx, packet)
        }
    }

    protected abstract fun handshake(ctx: ChannelHandlerContext, packet: Packet)
    protected abstract fun status(ctx: ChannelHandlerContext, packet: Packet)
    protected abstract fun login(ctx: ChannelHandlerContext, packet: Packet)
    protected abstract fun play(ctx: ChannelHandlerContext, packet: Packet)
}