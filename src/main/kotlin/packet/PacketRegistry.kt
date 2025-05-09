package dev.spaghett.packet

object PacketRegistry {
    private val registry = mutableMapOf<PacketMeta, () -> Packet>()

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
}