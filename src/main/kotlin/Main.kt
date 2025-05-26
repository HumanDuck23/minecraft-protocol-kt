package dev.spaghett

import dev.spaghett.netty.handlers.v1_8.DefaultServerHandler
import dev.spaghett.netty.instance.Server
import dev.spaghett.netty.instance.ServerConfiguration
import dev.spaghett.packet.ProtocolVersion

fun main() {
    val config = ServerConfiguration(version = ProtocolVersion.V1_8)
    val server = Server(config) { DefaultServerHandler(config) }
    server.start()
}