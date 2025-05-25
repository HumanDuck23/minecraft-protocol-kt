package dev.spaghett.protocol.play.client

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.VarIntValue

class C16ClientStatus : Packet(PacketMeta(0x16, PacketDirection.FROM_CLIENT, ProtocolState.PLAY)) {
    var action by VarIntValue(this)
}