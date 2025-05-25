package dev.spaghett.protocol.status.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.LongValue

class S01StatusPong : Packet(PacketMeta(0x01, PacketDirection.FROM_SERVER, ProtocolState.STATUS)) {
    var payload by LongValue(this)
}