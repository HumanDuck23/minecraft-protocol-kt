package dev.spaghett.netty.codec

import dev.spaghett.utils.ZLib
import dev.spaghett.utils.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class FramingEncoder(private var threshold: Int = -1) : MessageToByteEncoder<ByteBuf>() {

    fun setThreshold(threshold: Int) {
        this.threshold = threshold
    }

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        if (threshold == -1) {
            // Compression disabled: write varint length, then raw data
            out.writeVarInt(msg.readableBytes())
            out.writeBytes(msg)
            return
        }

        if (msg.readableBytes() < threshold) {
            // Packet below threshold: no compression
            val length = getVarIntSize(0) + msg.readableBytes()
            out.writeVarInt(length)          // packet length
            out.writeVarInt(0)               // uncompressed marker
            out.writeBytes(msg)              // raw data
        } else {
            // Compress the packet
            val inputBytes = ByteArray(msg.readableBytes())
            msg.readBytes(inputBytes)

            val compressed = ZLib.compressZlib(inputBytes)
            val length = getVarIntSize(inputBytes.size) + compressed.size

            out.writeVarInt(length)          // total length
            out.writeVarInt(inputBytes.size) // uncompressed length
            out.writeBytes(compressed)
        }
    }

    private fun getVarIntSize(value: Int): Int {
        var v = value
        var size = 0
        do {
            v = v ushr 7
            size++
        } while (v != 0)
        return size
    }
}