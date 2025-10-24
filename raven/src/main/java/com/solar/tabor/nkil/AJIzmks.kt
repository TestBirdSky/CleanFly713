package com.solar.tabor.nkil

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Date：2025/10/17
 * Describe:
 */

class AJIzmks {

    fun facebookDecode(keyAes: String, inputBytes: ByteArray): ByteArray {
        val key = SecretKeySpec(keyAes.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBytes = cipher.doFinal(inputBytes)
        return outputBytes
    }
}