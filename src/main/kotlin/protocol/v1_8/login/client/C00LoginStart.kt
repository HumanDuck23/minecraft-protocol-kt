package dev.spaghett.protocol.v1_8.login.client

import dev.spaghett.packet.*
import dev.spaghett.packet.values.StringValue

class C00LoginStart : Packet(PacketMeta(0x00, PacketDirection.FROM_CLIENT, ProtocolState.LOGIN, ProtocolVersion.V1_8)) {
    var username by StringValue(this)
}