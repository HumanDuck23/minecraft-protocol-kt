package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class BooleanValue(parent: Packet, value: Boolean = false, readPredicate: (() -> Boolean)? = null) :
    PacketValue<Boolean>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        value = buffer.readBoolean()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(value)
    }
}