package dev.spaghett.protocol.v1_8.play.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.PositionValue

class S05SpawnPosition : Packet(PacketMeta(0x05, PacketDirection.FROM_SERVER, ProtocolState.PLAY, ProtocolVersion.V1_8)) {
    var location by PositionValue(this)
}