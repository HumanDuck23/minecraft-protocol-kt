package dev.spaghett.protocol.play.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.PositionValue

class S05SpawnPosition : Packet(PacketMeta(0x05, PacketDirection.FROM_SERVER, ProtocolState.PLAY)) {
    var location by PositionValue(this)
}