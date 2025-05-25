package dev.spaghett.protocol.login.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.StringValue

class S02LoginSuccess : Packet(PacketMeta(0x02, PacketDirection.FROM_SERVER, ProtocolState.LOGIN)) {
    var uuid by StringValue(this)
    var username by StringValue(this)
}