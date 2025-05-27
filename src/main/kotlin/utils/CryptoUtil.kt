package dev.spaghett.utils

import java.math.BigInteger
import java.security.*

import javax.crypto.Cipher

object CryptoUtil {
    val keyPair: KeyPair = KeyPairGenerator
        .getInstance("RSA")
        .apply { initialize(1024) }
        .genKeyPair()

    private val random = SecureRandom()

    fun newVerifyToken(): ByteArray =
        ByteArray(4).also { random.nextBytes(it) }

    fun rsaDecrypt(data: ByteArray): ByteArray =
        Cipher.getInstance("RSA").apply {
            init(Cipher.DECRYPT_MODE, keyPair.private)
        }.doFinal(data)

    fun digestServerId(serverId: String,
                       publicKey: PublicKey,
                       sharedSecret: ByteArray): String {
        val sha1 = MessageDigest.getInstance("SHA-1")
        sha1.update(serverId.toByteArray(Charsets.ISO_8859_1))
        sha1.update(sharedSecret)
        sha1.update(publicKey.encoded)
        // as signed big-integer to hex (note leading zeros can be omitted)
        return BigInteger(sha1.digest()).toString(16)
    }
}