package dev.spaghett.netty

import dev.spaghett.protocol.GameProfile
import io.netty.util.AttributeKey

val GAME_PROFILE_KEY: AttributeKey<GameProfile> = AttributeKey.valueOf("gameProfile")