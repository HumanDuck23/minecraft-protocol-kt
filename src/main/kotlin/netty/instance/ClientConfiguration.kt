package dev.spaghett.netty.instance

import dev.spaghett.packet.ProtocolVersion
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession.FullJavaSession

data class ClientConfiguration(
    val version: ProtocolVersion,
    val host: String = "localhost",
    val port: Int = 25565,
    val username: String = "Player",
    val authenticate: Boolean = false,
    var session: FullJavaSession? = null // don't set this manually
)
