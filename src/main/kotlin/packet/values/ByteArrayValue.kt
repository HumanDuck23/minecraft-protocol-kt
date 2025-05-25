package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class ByteArrayValue(
    parent: Packet,
    value: ByteArray = byteArrayOf(),
    readPredicate: (() -> Boolean)? = null
) : PacketValue<ByteArray>(parent, value, readPredicate) {

    override fun read(buffer: ByteBuf) {
        val length = buffer.readableBytes()
        val bytes = ByteArray(length)
        buffer.readBytes(bytes)
        value = bytes
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeBytes(value)
    }
}
