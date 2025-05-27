package dev.spaghett.protocol.v1_8.login.client

import dev.spaghett.packet.*
import dev.spaghett.packet.values.ByteArrayValue
import dev.spaghett.packet.values.VarIntValue

class C01EncryptionResponse : Packet(PacketMeta(0x01, PacketDirection.FROM_CLIENT, ProtocolState.LOGIN, ProtocolVersion.V1_8)) {
    var sharedSecretLength by VarIntValue(this)
    var sharedSecret by ByteArrayValue(this, { sharedSecretLength })
    var verifyTokenLength by VarIntValue(this)
    var verifyToken by ByteArrayValue(this, { verifyTokenLength })
}