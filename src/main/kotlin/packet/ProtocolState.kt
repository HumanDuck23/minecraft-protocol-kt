package dev.spaghett.packet

enum class ProtocolState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    PLAY,
    CONFIGURATION;

    companion object {
        @JvmStatic
        fun fromId(id: Int): ProtocolState {
            return entries.find { it.ordinal == id } ?: throw IllegalArgumentException("Unknown protocol state id $id")
        }
    }
}