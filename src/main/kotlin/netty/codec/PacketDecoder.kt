package dev.spaghett.netty.codec

import dev.spaghett.netty.STATE_KEY
import dev.spaghett.packet.*
import dev.spaghett.utils.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.slf4j.LoggerFactory

class PacketDecoder(
    private val direction: PacketDirection,
    private val version: ProtocolVersion
) : ByteToMessageDecoder() {
    private val logger = LoggerFactory.getLogger(PacketDecoder::class.java)

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        val packetId = buf.readVarInt()
        val state = ctx.channel().attr(STATE_KEY).get()!!
        val meta = PacketMeta(packetId, direction, state, version)

        val packet = PacketRegistry.create(meta)

        if (packet == null) {
            //logger.warn("Unknown packet received: {} (buffer size: {})", meta, buf.readableBytes())
            val raw = buf.readRetainedSlice(buf.readableBytes())
            out += RawPacket(meta, raw)
            return
        }

        try {
            packet.read(buf)
            out += packet
        } catch (e: Exception) {
            logger.error("Failed to decode packet {}: {}", meta, e.message, e)
        }
    }
}