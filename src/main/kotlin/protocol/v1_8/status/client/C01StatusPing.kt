package dev.spaghett.protocol.v1_8.status.client

import dev.spaghett.packet.*
import dev.spaghett.packet.values.LongValue

class C01StatusPing : Packet(PacketMeta(0x01, PacketDirection.FROM_CLIENT, ProtocolState.STATUS, ProtocolVersion.V1_8)) {
    var payload by LongValue(this)
}