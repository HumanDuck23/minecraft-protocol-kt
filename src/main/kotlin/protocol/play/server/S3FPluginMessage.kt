package dev.spaghett.protocol.play.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.ByteArrayValue
import dev.spaghett.packet.values.StringValue

class S3FPluginMessage : Packet(PacketMeta(0x3F, PacketDirection.FROM_SERVER, ProtocolState.PLAY)) {
    var channel by StringValue(this)
    var data by ByteArrayValue(this)
}