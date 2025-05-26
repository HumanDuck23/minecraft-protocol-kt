package dev.spaghett.protocol.v1_8.handshake.client

import dev.spaghett.packet.*
import dev.spaghett.packet.values.StringValue
import dev.spaghett.packet.values.UShortValue
import dev.spaghett.packet.values.VarIntValue

class C00Handshake : Packet(PacketMeta(0x00, PacketDirection.FROM_CLIENT, ProtocolState.HANDSHAKE, ProtocolVersion.V1_8)) {
    var protocolVersion by VarIntValue(this)
    var serverAddress by StringValue(this)
    var serverPort by UShortValue(this)
    var nextState by VarIntValue(this)
}