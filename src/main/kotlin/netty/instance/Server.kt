package dev.spaghett.netty.instance

import dev.spaghett.netty.STATE_KEY
import dev.spaghett.netty.codec.LengthDecoder
import dev.spaghett.netty.codec.LengthEncoder
import dev.spaghett.netty.codec.PacketDecoder
import dev.spaghett.netty.codec.PacketEncoder
import dev.spaghett.netty.handlers.ServerHandler
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.ProtocolState
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress
import java.net.SocketAddress

class Server (
    private val port: SocketAddress = InetSocketAddress(25565)
) {
    private val bossGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())
    private val workerGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())

    fun start() {
        try {
            ServerBootstrap().apply {
                group(bossGroup, workerGroup)
                channel(NioServerSocketChannel::class.java)
                childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.attr(STATE_KEY).set(ProtocolState.HANDSHAKE)

                        val p = ch.pipeline()

                        p.addLast("lengthDecoder", LengthDecoder())
                        p.addLast("packetDecoder", PacketDecoder(PacketDirection.FROM_CLIENT))

                        p.addLast("lengthEncoder", LengthEncoder())
                        p.addLast("packetEncoder", PacketEncoder())

                        p.addLast("handler", ServerHandler())
                    }
                })
            }.bind(port).sync().also {
                println("Server started on port $port")
                it.channel().closeFuture().sync()
            }
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}