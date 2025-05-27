package dev.spaghett.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.lenni0451.commons.httpclient.HttpClient
import net.raphimc.minecraftauth.MinecraftAuth
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession.FullJavaSession
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode.MsaDeviceCode
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode.MsaDeviceCodeCallback
import java.io.File


class AuthManager(private val username: String) {
    private val cacheFile = File(getMinecraftFolder(), "auth_cache_$username.json")
    private val gson = Gson()

    fun auth(): FullJavaSession {
        val httpClient: HttpClient = MinecraftAuth.createHttpClient()

        if (cacheFile.exists()) {
            val sessionJson = gson.fromJson(cacheFile.readText(), JsonObject::class.java)
            val session = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.fromJson(sessionJson)
            try {
                val readyToUseSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(httpClient, session)
                cacheFile.writeText(gson.toJson(MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(readyToUseSession)))
                return readyToUseSession
            } catch (e: Exception) {
                // This session is not valid, reauthenticate
            }
        }

        val javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(
            httpClient,
            MsaDeviceCodeCallback { msaDeviceCode: MsaDeviceCode ->
                println("Go to " + msaDeviceCode.verificationUri)
                println("Enter code " + msaDeviceCode.userCode)

                println("Go to " + msaDeviceCode.directVerificationUri)
            })

        val serializedSession: JsonObject = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(javaSession)
        cacheFile.writeText(gson.toJson(serializedSession))
        return javaSession
    }

    private fun getMinecraftFolder(): File {
        val osName = System.getProperty("os.name").lowercase()
        val home = System.getProperty("user.home")
        return when {
            osName.contains("win") -> {
                val appData = System.getenv("APPDATA") ?: home
                File(appData, ".minecraft")
            }
            osName.contains("mac") -> File(home, "Library/Application Support/minecraft")
            else -> File(home, ".minecraft")
        }
    }
}