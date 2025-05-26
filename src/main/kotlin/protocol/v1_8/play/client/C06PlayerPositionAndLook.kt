package dev.spaghett.protocol.v1_8.play.client

import dev.spaghett.packet.*
import dev.spaghett.packet.values.BooleanValue
import dev.spaghett.packet.values.DoubleValue
import dev.spaghett.packet.values.FloatValue

class C06PlayerPositionAndLook : Packet(PacketMeta(0x06, PacketDirection.FROM_CLIENT, ProtocolState.PLAY, ProtocolVersion.V1_8)) {
    var x by DoubleValue(this)
    var feetY by DoubleValue(this)
    var z by DoubleValue(this)
    var yaw by FloatValue(this)
    var pitch by FloatValue(this)
    var onGround by BooleanValue(this)
}