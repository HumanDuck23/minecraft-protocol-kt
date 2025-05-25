package dev.spaghett.protocol.status.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.StringValue

class S00StatusResponse : Packet(PacketMeta(0x00, PacketDirection.FROM_SERVER, ProtocolState.STATUS)) {
    var json by StringValue(this)
}