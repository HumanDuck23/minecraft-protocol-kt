package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import dev.spaghett.utils.readVarInt
import dev.spaghett.utils.writeVarInt
import io.netty.buffer.ByteBuf

class VarIntValue(parent: Packet, value: Int = 0, readPredicate: (() -> Boolean)? = null) :
    PacketValue<Int>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        value = buffer.readVarInt()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(value)
    }
}