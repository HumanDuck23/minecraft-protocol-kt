package dev.spaghett.packet

import dev.spaghett.protocol.handshake.client.C00Handshake
import dev.spaghett.protocol.login.client.C00LoginStart
import dev.spaghett.protocol.login.server.S00Disconnect
import dev.spaghett.protocol.login.server.S02LoginSuccess
import dev.spaghett.protocol.play.client.C06PlayerPositionAndLook
import dev.spaghett.protocol.play.client.C16ClientStatus
import dev.spaghett.protocol.play.client.C17PluginMessage
import dev.spaghett.protocol.play.server.*
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
    ///// Client Packets /////

    // Handshake
    registerPacket { C00Handshake() }
    registerPacket { C00StatusRequest() }

    // Status
    registerPacket { C01StatusPing() }

    // Login
    registerPacket { C00LoginStart() }

    // Play
    registerPacket { C06PlayerPositionAndLook() }
    registerPacket { C16ClientStatus() }
    registerPacket { C17PluginMessage() }

    ///// Server Packets /////

    // Status
    registerPacket { S00StatusResponse() }
    registerPacket { S01StatusPong() }

    // Login
    registerPacket { S00Disconnect() }
    registerPacket { S02LoginSuccess() }

    // Play
    registerPacket { S01JoinGame() }
    registerPacket { S3FPluginMessage() }
    registerPacket { S05SpawnPosition() }
    registerPacket { S08PlayerPositionAndLook() }
    registerPacket { S39PlayerAbilities() }
    registerPacket { S41ServerDifficulty() }
}