package dev.spaghett.protocol.v1_8.login.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.StringValue

class S00Disconnect : Packet(PacketMeta(0x00, PacketDirection.FROM_SERVER, ProtocolState.LOGIN, ProtocolVersion.V1_8)) {
    var reason by StringValue(this)
}