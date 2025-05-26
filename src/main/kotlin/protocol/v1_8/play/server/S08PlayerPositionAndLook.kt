package dev.spaghett.protocol.v1_8.play.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.ByteValue
import dev.spaghett.packet.values.DoubleValue
import dev.spaghett.packet.values.FloatValue

class S08PlayerPositionAndLook : Packet(PacketMeta(0x08, PacketDirection.FROM_SERVER, ProtocolState.PLAY, ProtocolVersion.V1_8)) {
    var x by DoubleValue(this)
    var y by DoubleValue(this)
    var z by DoubleValue(this)
    var yaw by FloatValue(this)
    var pitch by FloatValue(this)
    var flags by ByteValue(this)
}