package dev.spaghett.protocol.status.client

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.LongValue

class C01StatusPing : Packet(PacketMeta(0x01, PacketDirection.FROM_CLIENT, ProtocolState.STATUS)) {
    var payload by LongValue(this)
}