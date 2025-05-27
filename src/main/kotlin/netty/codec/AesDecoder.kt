package dev.spaghett.netty.codec

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import javax.crypto.Cipher

class AesDecoder(private val cipher: Cipher) : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, inBuf: ByteBuf, out: MutableList<Any>) {
        if (!inBuf.isReadable) return
        val arr = ByteArray(inBuf.readableBytes())
        inBuf.readBytes(arr)
        val plain = cipher.update(arr)
        out += Unpooled.wrappedBuffer(plain)
    }
}