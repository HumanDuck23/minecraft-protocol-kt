package dev.spaghett.packet

data class PacketMeta(
    val id: Int,
    val direction: PacketDirection,
    val state: ProtocolState
) {
    override fun toString(): String {
        return "PacketMeta(id=0x${id.toString(16)}, direction=$direction, state=$state)"
    }
}
