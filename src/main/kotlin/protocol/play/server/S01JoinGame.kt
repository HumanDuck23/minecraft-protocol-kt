package dev.spaghett.protocol.play.server

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState
import dev.spaghett.packet.values.*

class S01JoinGame : Packet(PacketMeta(0x01, PacketDirection.FROM_SERVER, ProtocolState.PLAY)) {
    var entityId by IntValue(this)
    var gamemode by UByteValue(this)
    var dimension by ByteValue(this)
    var difficulty by UByteValue(this)
    var maxPlayers by UByteValue(this)
    var levelType by StringValue(this)
    var reducedDebugInfo by BooleanValue(this)
}