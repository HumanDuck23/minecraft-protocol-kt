package dev.spaghett

import dev.spaghett.netty.instance.Server
import dev.spaghett.netty.instance.ServerConfiguration

fun main() {
    val server = Server(ServerConfiguration())
    server.start()
}