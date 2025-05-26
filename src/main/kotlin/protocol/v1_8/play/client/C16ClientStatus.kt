package dev.spaghett.protocol.v1_8.play.client

import dev.spaghett.packet.*
import dev.spaghett.packet.values.VarIntValue

class C16ClientStatus : Packet(PacketMeta(0x16, PacketDirection.FROM_CLIENT, ProtocolState.PLAY, ProtocolVersion.V1_8)) {
    var action by VarIntValue(this)
}