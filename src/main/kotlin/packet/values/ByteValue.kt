package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class ByteValue(parent: Packet, value: Byte = 0, readPredicate: (() -> Boolean)? = null) :
    PacketValue<Byte>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        value = buffer.readByte()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeByte(value.toInt())
    }
}