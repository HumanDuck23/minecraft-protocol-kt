package dev.spaghett.protocol.play.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.UByteValue

class S41ServerDifficulty : Packet(PacketMeta(0x41, PacketDirection.FROM_SERVER, ProtocolState.PLAY)) {
    var difficulty by UByteValue(this)
}