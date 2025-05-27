package dev.spaghett.protocol.v1_8.login.server

import dev.spaghett.packet.*
import dev.spaghett.packet.values.ByteArrayValue
import dev.spaghett.packet.values.StringValue
import dev.spaghett.packet.values.VarIntValue

class S01EncryptionRequest : Packet(PacketMeta(0x01, PacketDirection.FROM_SERVER, ProtocolState.LOGIN, ProtocolVersion.V1_8)) {
    var serverID by StringValue(this)
    var publicKeyLength by VarIntValue(this)
    var publicKey by ByteArrayValue(this, { publicKeyLength })
    var verifyTokenLength by VarIntValue(this)
    var verifyToken by ByteArrayValue(this, { verifyTokenLength })
}