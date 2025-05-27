package dev.spaghett.protocol.v1_8.login.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.VarIntValue

class S03SetCompression : Packet(PacketMeta(0x03, PacketDirection.FROM_SERVER, ProtocolState.LOGIN, ProtocolVersion.V1_8)) {
    var threshold by VarIntValue(this)
}