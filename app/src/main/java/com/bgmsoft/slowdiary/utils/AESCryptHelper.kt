package com.bgmsoft.slowdiary.utils

import android.util.Base64
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private val ivBytes = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)

@Throws(java.io.UnsupportedEncodingException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, InvalidAlgorithmParameterException::class, IllegalBlockSizeException::class, BadPaddingException::class)
fun AES_Encode(str: String, key: String): String {
    val textBytes = str.toByteArray(charset("UTF-8"))
    val ivSpec = IvParameterSpec(ivBytes)
    val newKey = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec)
    return Base64.encodeToString(cipher.doFinal(textBytes), 0)
}

@Throws(java.io.UnsupportedEncodingException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, InvalidAlgorithmParameterException::class, IllegalBlockSizeException::class, BadPaddingException::class)
fun AES_Decode(str: String, key: String): String {
    val textBytes = Base64.decode(str, 0)
    // byte[] textBytes = str.getBytes("UTF-8");
    val ivSpec = IvParameterSpec(ivBytes)
    val newKey = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec)
    return String(cipher.doFinal(textBytes), Charsets.UTF_8)
}


private val ivBytes2 = byteArrayOf(0x01, 0x04, 0x23, 0x24, 0x58, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10)

fun AES_Encode(str: String): String {
    var result = str
    try {
        val textBytes = str.toByteArray(charset("UTF-8"))
        val ivSpec = IvParameterSpec(ivBytes2)
        val newKey = SecretKeySpec("lineable".toByteArray(charset("UTF-8")), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec)
        result = Base64.encodeToString(cipher.doFinal(textBytes), 0)
    } catch (e: Exception) {
    }

    return result
}

fun AES_Decode(str: String): String {
    var result = str
    try {
        val textBytes = Base64.decode(str, 0)
        val ivSpec = IvParameterSpec(ivBytes2)
        val newKey = SecretKeySpec("lineable".toByteArray(charset("UTF-8")), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec)
        result = String(cipher.doFinal(textBytes), Charsets.UTF_8)
    } catch (e: Exception) {
    }

    return result
}