package dev.spaghett.packet

data class PacketMeta(
    val id: Int,
    val direction: PacketDirection,
    val state: ProtocolState
)
