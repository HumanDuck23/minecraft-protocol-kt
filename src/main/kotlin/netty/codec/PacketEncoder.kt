package dev.spaghett.netty.codec

import dev.spaghett.packet.Packet
import dev.spaghett.utils.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketEncoder : MessageToByteEncoder<Packet>() {
    override fun encode(ctx: ChannelHandlerContext, packet: Packet, out: ByteBuf) {
        out.writeVarInt(packet.meta.id)
        packet.write(out)
    }
}