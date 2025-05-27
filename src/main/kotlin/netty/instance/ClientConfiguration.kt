package dev.spaghett.netty.instance

import dev.spaghett.packet.ProtocolVersion

data class ClientConfiguration(
    val version: ProtocolVersion,
    val host: String = "localhost",
    val port: Int = 25565,
    val username: String = "Player",
    val authenticate: Boolean = false,
)
