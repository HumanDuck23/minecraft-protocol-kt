package dev.spaghett.netty.codec

import dev.spaghett.utils.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class LengthDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        buf.markReaderIndex()
        if (!buf.isReadable) return

        try {
            val length = buf.readVarInt()

            if (buf.readableBytes() < length) {
                buf.resetReaderIndex()
                return
            }

            out += buf.readRetainedSlice(length)
        } catch (e: IllegalArgumentException) {
            buf.resetReaderIndex()
            return
        }
    }
}