package dev.spaghett.protocol.play.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.ByteValue
import dev.spaghett.packet.values.FloatValue

class S39PlayerAbilities : Packet(PacketMeta(0x39, PacketDirection.FROM_SERVER, ProtocolState.PLAY)) {
    var flags by ByteValue(this)
    var flyingSpeed by FloatValue(this)
    var fovModifier by FloatValue(this)
}