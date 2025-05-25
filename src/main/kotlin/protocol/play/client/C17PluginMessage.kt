package dev.spaghett.protocol.play.client

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.ByteArrayValue
import dev.spaghett.packet.values.StringValue

class C17PluginMessage : Packet(PacketMeta(0x17, PacketDirection.FROM_CLIENT, ProtocolState.PLAY)) {
    var channel by StringValue(this)
    var data by ByteArrayValue(this)
}