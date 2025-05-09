package dev.spaghett.protocol.handshaking.client

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.StringValue
import dev.spaghett.packet.values.UShortValue
import dev.spaghett.packet.values.VarIntValue

class C00Handshake : Packet(PacketMeta(0x00, PacketDirection.FROM_CLIENT, ProtocolState.HANDSHAKE)) {
    var protocolVersion by VarIntValue(this)
    var serverAddress by StringValue(this)
    var serverPort by UShortValue(this)
    var nextState by VarIntValue(this)
}