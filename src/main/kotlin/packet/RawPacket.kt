package dev.spaghett.packet

import io.netty.buffer.ByteBuf

class RawPacket(
    override val meta: PacketMeta,
    private val rawData: ByteBuf
) : Packet(meta) {
    override fun read(buf: ByteBuf) {
        // nothing to read, raw data is already provided
    }

    override fun write(buf: ByteBuf) {
        buf.writeBytes(rawData.slice())
    }
}