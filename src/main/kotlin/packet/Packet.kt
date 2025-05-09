package dev.spaghett.packet

import io.netty.buffer.ByteBuf

abstract class Packet(val meta: PacketMeta) {
    private val fields = mutableListOf<PacketValue<*>>()

    fun registerField(field: PacketValue<*>) {
        fields += field
    }

    fun read(buf: ByteBuf) {
        for (field in fields) {
            if (field.readPredicate?.invoke() != false) {
                field.read(buf)
            }
        }
    }

    fun write(buf: ByteBuf) {
        for (field in fields) {
            if (field.readPredicate?.invoke() != false) {
                field.write(buf)
            }
        }
    }
}