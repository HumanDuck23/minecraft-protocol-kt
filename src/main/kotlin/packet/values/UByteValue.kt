package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class UByteValue(parent: Packet, value: Int = 0, readPredicate: (() -> Boolean)? = null) :
    PacketValue<Int>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        value = buffer.readUnsignedByte().toInt()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeByte(value)
    }
}