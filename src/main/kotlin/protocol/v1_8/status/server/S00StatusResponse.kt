package dev.spaghett.protocol.v1_8.status.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.StringValue

class S00StatusResponse : Packet(PacketMeta(0x00, PacketDirection.FROM_SERVER, ProtocolState.STATUS, ProtocolVersion.V1_8)) {
    var json by StringValue(this)
}