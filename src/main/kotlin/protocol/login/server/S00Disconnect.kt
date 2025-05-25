package dev.spaghett.protocol.login.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.StringValue

class S00Disconnect : Packet(PacketMeta(0x00, PacketDirection.FROM_SERVER, ProtocolState.LOGIN)) {
    var reason by StringValue(this)
}