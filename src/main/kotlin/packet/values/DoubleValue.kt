package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class DoubleValue(
    parent: Packet,
    value: Double = 0.0,
    readPredicate: (() -> Boolean)? = null
) : PacketValue<Double>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        value = buffer.readDouble()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeDouble(value)
    }
}