package dev.spaghett.protocol.v1_8.play.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.*

class S01JoinGame : Packet(PacketMeta(0x01, PacketDirection.FROM_SERVER, ProtocolState.PLAY, ProtocolVersion.V1_8)) {
    var entityId by IntValue(this)
    var gamemode by UByteValue(this)
    var dimension by ByteValue(this)
    var difficulty by UByteValue(this)
    var maxPlayers by UByteValue(this)
    var levelType by StringValue(this)
    var reducedDebugInfo by BooleanValue(this)
}