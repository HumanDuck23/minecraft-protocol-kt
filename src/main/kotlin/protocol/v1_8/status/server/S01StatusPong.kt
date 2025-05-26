package dev.spaghett.protocol.v1_8.status.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.LongValue

class S01StatusPong : Packet(PacketMeta(0x01, PacketDirection.FROM_SERVER, ProtocolState.STATUS, ProtocolVersion.V1_8)) {
    var payload by LongValue(this)
}