package dev.spaghett.netty.codec

import dev.spaghett.utils.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class LengthEncoder : MessageToByteEncoder<ByteBuf>() {
    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val length = msg.readableBytes()
        out.writeVarInt(length)
        out.writeBytes(msg, msg.readerIndex(), length)
    }
}