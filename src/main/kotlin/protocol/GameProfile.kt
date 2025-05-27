package dev.spaghett.protocol

import java.util.UUID

data class GameProfile(
    val id: UUID,
    val name: String,
    val properties: List<Property> = listOf()
)

data class Property(
    val name: String,
    val value: String,
    val signature: String?
)
