package dev.spaghett.protocol.v1_8.play.client

import dev.spaghett.packet.*
import dev.spaghett.packet.values.ByteArrayValue
import dev.spaghett.packet.values.StringValue

class C17PluginMessage : Packet(PacketMeta(0x17, PacketDirection.FROM_CLIENT, ProtocolState.PLAY, ProtocolVersion.V1_8)) {
    var channel by StringValue(this)
    var data by ByteArrayValue(this)
}