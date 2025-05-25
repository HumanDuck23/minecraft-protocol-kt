package dev.spaghett.packet

import dev.spaghett.protocol.handshake.client.C00Handshake
import dev.spaghett.protocol.status.client.C00StatusRequest
import dev.spaghett.protocol.status.client.C01StatusPing
import dev.spaghett.protocol.status.server.S00StatusResponse
import dev.spaghett.protocol.status.server.S01StatusPong

object PacketRegistry {

    private val registry = mutableMapOf<PacketMeta, () -> Packet>()

    init {
        registerPackets()
    }

    fun register(meta: PacketMeta, constructor: () -> Packet) {
        registry[meta] = constructor
    }

    fun create(meta: PacketMeta): Packet? {
        return registry[meta]?.invoke()
    }
}

inline fun <reified T : Packet> registerPacket(noinline constructor: () -> T) {
    val meta = constructor().meta
    PacketRegistry.register(meta, constructor)
}

fun registerPackets() {
    // Client Packets
    registerPacket { C00Handshake() }
    registerPacket { C00StatusRequest() }
    registerPacket { C01StatusPing() }

    // Server Packets
    registerPacket { S00StatusResponse() }
    registerPacket { S01StatusPong() }
}