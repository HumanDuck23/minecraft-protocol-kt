package dev.spaghett.packet

import io.netty.buffer.ByteBuf

abstract class Packet(open val meta: PacketMeta) {
    private val fields = mutableListOf<PacketValue<*>>()

    fun registerField(field: PacketValue<*>) {
        fields += field
    }

    open fun read(buf: ByteBuf) {
        for (field in fields) {
            if (field.readPredicate?.invoke() != false) {
                field.read(buf)
            }
        }
    }

    open fun write(buf: ByteBuf) {
        for (field in fields) {
            if (field.readPredicate?.invoke() != false) {
                field.write(buf)
            }
        }
    }
}