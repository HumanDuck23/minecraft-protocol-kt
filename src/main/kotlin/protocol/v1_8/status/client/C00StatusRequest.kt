package dev.spaghett.protocol.v1_8.status.client

import dev.spaghett.packet.*

class C00StatusRequest : Packet(PacketMeta(0x00, PacketDirection.FROM_CLIENT, ProtocolState.STATUS, ProtocolVersion.V1_8)) {}