package dev.spaghett.netty

import io.netty.util.AttributeKey

val VERIFY_TOKEN_KEY: AttributeKey<ByteArray> = AttributeKey.valueOf("verifyToken")