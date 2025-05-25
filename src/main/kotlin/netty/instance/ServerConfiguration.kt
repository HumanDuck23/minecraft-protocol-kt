package dev.spaghett.netty.instance

data class ServerConfiguration(
    val port: Int = 25565,
    val maxPlayers: Int = 20,
    val motd: String = "A Minecraft Server",
    val onlineMode: Boolean = false,
)
