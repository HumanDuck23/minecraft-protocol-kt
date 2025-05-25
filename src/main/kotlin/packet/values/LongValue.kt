package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class LongValue(parent: Packet, value: Long = 0L, readPredicate: (() -> Boolean)? = null) :
    PacketValue<Long>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        value = buffer.readLong()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeLong(value)
    }
}