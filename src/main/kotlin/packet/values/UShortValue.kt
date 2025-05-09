package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class UShortValue(parent: Packet, value: UShort = 0u, readPredicate: (() -> Boolean)? = null) :
    PacketValue<UShort>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        value = buffer.readUnsignedShort().toUShort()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeShort(value.toInt())
    }
}