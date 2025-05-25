package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class FloatValue(
    parent: Packet,
    value: Float = 0f,
    readPredicate: (() -> Boolean)? = null
) : PacketValue<Float>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        value = buffer.readFloat()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(value)
    }
}