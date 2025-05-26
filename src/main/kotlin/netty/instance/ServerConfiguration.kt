package dev.spaghett.netty.instance

import dev.spaghett.packet.ProtocolVersion

data class ServerConfiguration(
    val version: ProtocolVersion,
    val port: Int = 25565,
    val maxPlayers: Int = 20,
    val motd: String = "A Minecraft Server",
    val onlineMode: Boolean = false,
)
