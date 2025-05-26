package dev.spaghett.protocol.v1_8.play.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.UByteValue

class S41ServerDifficulty : Packet(PacketMeta(0x41, PacketDirection.FROM_SERVER, ProtocolState.PLAY, ProtocolVersion.V1_8)) {
    var difficulty by UByteValue(this)
}