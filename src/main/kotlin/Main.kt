package dev.spaghett

import dev.spaghett.netty.handlers.v1_8.DefaultClientHandler
import dev.spaghett.netty.handlers.v1_8.DefaultServerHandler
import dev.spaghett.netty.instance.Client
import dev.spaghett.netty.instance.ClientConfiguration
import dev.spaghett.netty.instance.Server
import dev.spaghett.netty.instance.ServerConfiguration
import dev.spaghett.packet.ProtocolVersion

fun main() {
//    val config = ServerConfiguration(version = ProtocolVersion.V1_8, port = 25566, onlineMode = false)
//    val server = Server(config) { DefaultServerHandler(config) }
//    server.start()
    val clientConfig = ClientConfiguration(version = ProtocolVersion.V1_8, port = 25567, host = "localhost", username = "_________B______", authenticate = true )
    val client = Client(clientConfig) { DefaultClientHandler(clientConfig) }
    client.connect()
}