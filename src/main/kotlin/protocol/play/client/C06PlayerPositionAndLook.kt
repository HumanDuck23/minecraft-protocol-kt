package dev.spaghett.protocol.play.client

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.BooleanValue
import dev.spaghett.packet.values.DoubleValue
import dev.spaghett.packet.values.FloatValue

class C06PlayerPositionAndLook : Packet(PacketMeta(0x06, PacketDirection.FROM_CLIENT, ProtocolState.PLAY)) {
    var x by DoubleValue(this)
    var feetY by DoubleValue(this)
    var z by DoubleValue(this)
    var yaw by FloatValue(this)
    var pitch by FloatValue(this)
    var onGround by BooleanValue(this)
}