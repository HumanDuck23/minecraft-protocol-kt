package dev.spaghett.packet.values

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketValue
import io.netty.buffer.ByteBuf

class ByteArrayValue(
    private val parent: Packet,
    private val lengthProvider: (() -> Int)? = null,
    value: ByteArray = byteArrayOf(),
    readPredicate: (() -> Boolean)? = null
) : PacketValue<ByteArray>(parent, value, readPredicate) {

    override fun read(buffer: ByteBuf) {
        val length = lengthProvider?.invoke()
            ?: buffer.readableBytes()       // no provider means everything left

        check(length >= 0) {
            "Invalid length ($length) for ${parent.meta} on ByteArrayValue"
        }
        check(buffer.readableBytes() >= length) {
            "Not enough bytes for ${parent.meta}: have=${buffer.readableBytes()}, need=$length"
        }

        val bytes = ByteArray(length)
        buffer.readBytes(bytes)
        value = bytes
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeBytes(value)
    }
}
