package dev.spaghett.netty.codec

import dev.spaghett.netty.STATE_KEY
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.PacketRegistry
import dev.spaghett.utils.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketDecoder(
    private val direction: PacketDirection
) : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        val packetId = buf.readVarInt()
        val state = ctx.channel().attr(STATE_KEY).get()!!
        val meta = PacketMeta(packetId, direction, state)

        val packet = PacketRegistry.create(meta)
            ?: throw IllegalStateException("Unknown packet $meta")

        packet.read(buf)
        out += packet
    }
}