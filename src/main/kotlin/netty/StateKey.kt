package dev.spaghett.netty

import dev.spaghett.packet.ProtocolState
import io.netty.util.AttributeKey

val STATE_KEY = AttributeKey.valueOf<ProtocolState>("protocolState")