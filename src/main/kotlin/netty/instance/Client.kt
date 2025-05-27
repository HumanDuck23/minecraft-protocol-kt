package dev.spaghett.netty.instance

import dev.spaghett.netty.STATE_KEY
import dev.spaghett.netty.codec.FramingDecoder
import dev.spaghett.netty.codec.FramingEncoder
import dev.spaghett.netty.codec.PacketDecoder
import dev.spaghett.netty.codec.PacketEncoder
import dev.spaghett.netty.handlers.PacketHandler
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.ProtocolState
import dev.spaghett.utils.AuthManager
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.slf4j.LoggerFactory


class Client(
    private val config: ClientConfiguration,
    private val handlerFactory: () -> PacketHandler
) {
    private val group = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())
    private val logger = LoggerFactory.getLogger(Client::class.java)
    private val authManager = AuthManager(config.username)

    fun connect() {
        if (config.authenticate) {
            try {
                val session = authManager.auth()
                config.session = session
            } catch (e: Exception) {
                logger.error("Authentication failed: ${e.message}", e)
                return
            }
        }
        try {
            val bootstrap = Bootstrap().apply {
                group(group)
                channel(NioSocketChannel::class.java)
                handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.attr(STATE_KEY).set(ProtocolState.HANDSHAKE)

                        val p = ch.pipeline()
                        p.addLast("framingDecoder", FramingDecoder())
                        p.addLast("packetDecoder", PacketDecoder(PacketDirection.FROM_SERVER, config.version))

                        p.addLast("framingEncoder", FramingEncoder())
                        p.addLast("packetEncoder", PacketEncoder())

                        p.addLast("handler",  handlerFactory())
                    }
                })
            }

            val future = bootstrap.connect(config.host, config.port).sync()
            logger.info("Connected to server at ${config.host}:${config.port}")

            val handler = future.channel().pipeline().get("handler") as PacketHandler
            handler.onConnectionOpened(future.channel().pipeline().context(handler))

            future.channel().closeFuture().sync()
        } catch (e: Exception) {
            logger.error("Failed to connect to server: ${e.message}", e)
        } finally {
            group.shutdownGracefully()
            logger.info("Client connection closed.")
        }
    }
}