package dev.spaghett.protocol.v1_8.play.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.ByteArrayValue
import dev.spaghett.packet.values.StringValue

class S3FPluginMessage : Packet(PacketMeta(0x3F, PacketDirection.FROM_SERVER, ProtocolState.PLAY, ProtocolVersion.V1_8)) {
    var channel by StringValue(this)
    var data by ByteArrayValue(this)
}