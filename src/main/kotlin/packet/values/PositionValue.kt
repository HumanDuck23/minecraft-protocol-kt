package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class PositionValue(parent: Packet, value: Triple<Int, Int, Int> = Triple(0, 0, 0), readPredicate: (() -> Boolean)? = null) :
    PacketValue<Triple<Int, Int, Int>>(parent, value, readPredicate) {
    override fun read(buffer: ByteBuf) {
        val packed = buffer.readLong()
        val x = (packed shr 38).toInt()
        val y = (packed shl 52 shr 52).toInt()
        val z = (packed shl 26 shr 38).toInt()
        value = Triple(x, y, z)
    }

    override fun write(buffer: ByteBuf) {
        val (x, y, z) = value
        val packed = ((x.toLong() and 0x3FFFFFF) shl 38) or
                ((z.toLong() and 0x3FFFFFF) shl 12) or
                (y.toLong() and 0xFFF)
        buffer.writeLong(packed)
    }
}