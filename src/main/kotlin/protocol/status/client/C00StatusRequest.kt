package dev.spaghett.protocol.status.client

import dev.spaghett.packet.Packet
import dev.spaghett.packet.PacketDirection
import dev.spaghett.packet.PacketMeta
import dev.spaghett.packet.ProtocolState

class C00StatusRequest : Packet(PacketMeta(0x00, PacketDirection.FROM_CLIENT, ProtocolState.STATUS)) {}