package dev.spaghett.protocol.play.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.ByteValue
import dev.spaghett.packet.values.DoubleValue
import dev.spaghett.packet.values.FloatValue

class S08PlayerPositionAndLook : Packet(PacketMeta(0x08, PacketDirection.FROM_SERVER, ProtocolState.PLAY)) {
    var x by DoubleValue(this)
    var y by DoubleValue(this)
    var z by DoubleValue(this)
    var yaw by FloatValue(this)
    var pitch by FloatValue(this)
    var flags by ByteValue(this)
}