package dev.spaghett.protocol.login.client

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.StringValue

class C00LoginStart : Packet(PacketMeta(0x00, PacketDirection.FROM_CLIENT, ProtocolState.LOGIN)) {
    var username by StringValue(this)
}