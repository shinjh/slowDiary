package com.bgmsoft.slowdiary.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object SignatureHelper {

    val HASH_TYPE = "SHA-256"
    val NUM_HASHED_BYTES = 9
    val NUM_BASE64_CHAR = 11

    fun getAppSignatures(context: Context): ArrayList<String> {
        val appCodes = ArrayList<String>()

        try {
            // Get all package signatures for the current package
            val packageName = context.packageName
            val packageManager = context.packageManager
            val signatures = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures

            // For each signature create a compatible hash
            for (signature in signatures) {
                val hash = getHash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(String.format("%s", hash))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            L.e("AppSignature Unable to find package to obtain hash. : $e")
        }

        return appCodes
    }

    private fun getHash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)

            // encode into Base64
            var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

            L.e("AppSignature  Package : $packageName, Hash : $base64Hash")
            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            L.e("AppSignature hash: NoSuchAlgorithm $e")
        }

        return null
    }
}