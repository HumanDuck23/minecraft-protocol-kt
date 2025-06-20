package dev.spaghett.netty.instance

import dev.spaghett.netty.STATE_KEY
import dev.spaghett.netty.codec.*
import dev.spaghett.netty.handlers.PacketHandler
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.ProtocolState
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.slf4j.LoggerFactory

class Server (
    private val config: ServerConfiguration,
    private val handlerFactory: () -> PacketHandler
) {
    private val bossGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())
    private val workerGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())

    private val logger = LoggerFactory.getLogger(Server::class.java)

    fun start() {
        try {
            ServerBootstrap().apply {
                group(bossGroup, workerGroup)
                channel(NioServerSocketChannel::class.java)
                childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.attr(STATE_KEY).set(ProtocolState.HANDSHAKE)

                        val p = ch.pipeline()

                        p.addLast("framingDecoder", FramingDecoder())
                        p.addLast("packetDecoder", PacketDecoder(PacketDirection.FROM_CLIENT, config.version))

                        p.addLast("framingEncoder", FramingEncoder())
                        p.addLast("packetEncoder", PacketEncoder())

                        val handler = handlerFactory()
                        p.addLast("handler", handler)
                        handler.onConnectionOpened(ch.pipeline().context(handler))
                    }
                })
            }.bind(config.port).sync().also {
                logger.info("Server started on port ${config.port}")
                it.channel().closeFuture().sync()
            }
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}