package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import dev.spaghett.utils.readUTF8
import dev.spaghett.utils.writeUTF8
import io.netty.buffer.ByteBuf

class StringValue(parent: Packet, value: String = "", readPredicate: (() -> Boolean)? = null) :
    PacketValue<String>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        value = buffer.readUTF8()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeUTF8(value)
    }
}