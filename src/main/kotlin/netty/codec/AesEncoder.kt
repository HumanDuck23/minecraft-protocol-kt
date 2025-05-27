package dev.spaghett.netty.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import javax.crypto.Cipher

class AesEncoder(private val cipher: Cipher) : MessageToByteEncoder<ByteBuf>() {
    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val arr = ByteArray(msg.readableBytes())
        msg.readBytes(arr)
        val encrypted = cipher.update(arr)
        out.writeBytes(encrypted)
    }
}