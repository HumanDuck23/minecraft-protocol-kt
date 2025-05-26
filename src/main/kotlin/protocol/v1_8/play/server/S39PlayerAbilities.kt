package dev.spaghett.protocol.v1_8.play.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.ByteValue
import dev.spaghett.packet.values.FloatValue

class S39PlayerAbilities : Packet(PacketMeta(0x39, PacketDirection.FROM_SERVER, ProtocolState.PLAY, ProtocolVersion.V1_8)) {
    var flags by ByteValue(this)
    var flyingSpeed by FloatValue(this)
    var fovModifier by FloatValue(this)
}