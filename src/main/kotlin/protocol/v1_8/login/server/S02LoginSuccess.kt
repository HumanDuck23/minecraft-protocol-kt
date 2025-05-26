package dev.spaghett.protocol.v1_8.login.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.StringValue

class S02LoginSuccess : Packet(PacketMeta(0x02, PacketDirection.FROM_SERVER, ProtocolState.LOGIN, ProtocolVersion.V1_8)) {
    var uuid by StringValue(this)
    var username by StringValue(this)
}