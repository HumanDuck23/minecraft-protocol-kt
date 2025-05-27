package dev.spaghett.utils

import com.google.gson.*
import dev.spaghett.protocol.GameProfile
import io.netty.channel.ChannelHandlerContext
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.Executors

object SessionServerUtil {
    private val logger = LoggerFactory.getLogger(SessionServerUtil::class.java)

    private val httpClient = OkHttpClient()
    private val gson = GsonBuilder()
        .registerTypeAdapter(
            UUID::class.java,
            object : JsonDeserializer<UUID> {
                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type,
                    context: JsonDeserializationContext
                ): UUID {
                    val s = json.asString
                    // insert hyphens just like before
                    val dashed = s.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)"
                            .toRegex(),
                        "$1-$2-$3-$4-$5"
                    )
                    return UUID.fromString(dashed)
                }
            })
        .create()

    private val authExecutor = Executors.newCachedThreadPool { r ->
        Thread(r, "auth-worker-${UUID.randomUUID()}").apply { isDaemon = true }
    }

    fun verifyWithSessionServerAsync(
        username: String,
        serverIdHash: String,
        ctx: ChannelHandlerContext,
        onSuccess: (GameProfile) -> Unit
    ) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("sessionserver.mojang.com")
            .addPathSegments("session/minecraft/hasJoined")
            .addQueryParameter("username", username)
            .addQueryParameter("serverId", serverIdHash)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        // dont block netty
        authExecutor.submit {
            try {
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw Exception("Unexpected response code: ${response.code}")
                    }

                    val body = response.body?.string()
                        ?: throw Exception("Response body is null")

                    val profile = gson.fromJson(body, GameProfile::class.java)
                    ctx.executor().execute { onSuccess(profile) }
                }
            } catch (e: Exception) {
                logger.error("Failed to verify session for user $username: ${e.message}", e)
                ctx.executor().execute {
                    ctx.close()
                }
            }
        }
    }

    fun joinSessionServer(accessToken: String, profileId: String, serverId: String, onSuccess: () -> Unit) {
        val json = """
            {
              "accessToken": "$accessToken",
              "selectedProfile": "$profileId",
              "serverId": "$serverId"
            }""".trimIndent()
        val req = Request.Builder()
            .url("https://sessionserver.mojang.com/session/minecraft/join")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        authExecutor.submit {
            httpClient.newCall(req).execute().use { resp ->
                if (resp.code != 204) {
                    throw IllegalStateException("Join failed: HTTP ${resp.code}")
                } else {
                    onSuccess()
                }
            }
        }
    }
}