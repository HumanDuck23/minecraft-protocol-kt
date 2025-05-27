package dev.spaghett.packet

object PacketRegistry {

    private val registry = mutableMapOf<PacketMeta, () -> Packet>()

    init {
        registerPackets1_8()
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

fun registerPackets1_8() {
    ///// Client Packets /////

    // Handshake
    registerPacket { dev.spaghett.protocol.v1_8.handshake.client.C00Handshake() }

    // Status
    registerPacket { dev.spaghett.protocol.v1_8.status.client.C00StatusRequest() }
    registerPacket { dev.spaghett.protocol.v1_8.status.client.C01StatusPing() }

    // Login
    registerPacket { dev.spaghett.protocol.v1_8.login.client.C00LoginStart() }
    registerPacket { dev.spaghett.protocol.v1_8.login.client.C01EncryptionResponse() }

    // Play
    registerPacket { dev.spaghett.protocol.v1_8.play.client.C06PlayerPositionAndLook() }
    registerPacket { dev.spaghett.protocol.v1_8.play.client.C16ClientStatus() }
    registerPacket { dev.spaghett.protocol.v1_8.play.client.C17PluginMessage() }

    ///// Server Packets /////

    // Status
    registerPacket { dev.spaghett.protocol.v1_8.status.server.S00StatusResponse() }
    registerPacket { dev.spaghett.protocol.v1_8.status.server.S01StatusPong() }

    // Login
    registerPacket { dev.spaghett.protocol.v1_8.login.server.S00Disconnect() }
    registerPacket { dev.spaghett.protocol.v1_8.login.server.S01EncryptionRequest() }
    registerPacket { dev.spaghett.protocol.v1_8.login.server.S02LoginSuccess() }

    // Play
    registerPacket { dev.spaghett.protocol.v1_8.play.server.S01JoinGame() }
    registerPacket { dev.spaghett.protocol.v1_8.play.server.S3FPluginMessage() }
    registerPacket { dev.spaghett.protocol.v1_8.play.server.S05SpawnPosition() }
    registerPacket { dev.spaghett.protocol.v1_8.play.server.S08PlayerPositionAndLook() }
    registerPacket { dev.spaghett.protocol.v1_8.play.server.S39PlayerAbilities() }
    registerPacket { dev.spaghett.protocol.v1_8.play.server.S41ServerDifficulty() }
}