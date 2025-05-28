package dev.spaghett.netty.codec

import dev.spaghett.utils.ZLib
import dev.spaghett.utils.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class FramingDecoder(private var threshold: Int = -1) : ByteToMessageDecoder() {

    fun setThreshold(threshold: Int) {
        this.threshold = threshold
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        buf.markReaderIndex()

        if (!buf.isReadable) return

        try {
            val packetLength = buf.readVarInt()

            // Wait until the full packet is available
            if (buf.readableBytes() < packetLength) {
                buf.resetReaderIndex()
                return
            }

            val packetData = buf.readRetainedSlice(packetLength)

            if (threshold == -1) {
                // Compression is disabled, pass along raw packet
                out += packetData
                return
            }

            val uncompressedLength = packetData.readVarInt()

            if (uncompressedLength == 0) {
                // Uncompressed packet (below threshold)
                out += packetData.readRetainedSlice(packetData.readableBytes())
            } else {
                // Compressed packet (>= threshold)
                val compressed = ByteArray(packetData.readableBytes())
                packetData.readBytes(compressed)

                val decompressed = ZLib.decompressZlib(compressed)
                out += ctx.alloc().buffer(decompressed.size).writeBytes(decompressed)
            }

        } catch (e: Exception) {
            buf.resetReaderIndex()
            e.printStackTrace()
        }
    }
}