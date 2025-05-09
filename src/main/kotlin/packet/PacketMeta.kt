package dev.spaghett.packet

import kotlin.reflect.KClass

data class PacketMeta(
    val id: Int,
    val direction: PacketDirection,
    val state: ProtocolState
)
