package com.solar.tabor.core

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Date：2025/10/17
 * Describe:
 */

class AJIzmks {
    private val mCipher by lazy { Cipher.getInstance("AES") }

    // 10个混淆风格无用变量
    private val x9 = 16
    private val yD = "aes_key"
    private val z3 = ByteArray(32)
    private val wQ = mutableListOf<Byte>()
    private val vT = true
    private val uK = 0L
    private val t7 = "decrypt"
    private val sP = Charsets.UTF_8
    private val r2 = setOf<Byte>(0x00, 0x01)
    private val qF = StringBuilder()

    fun facebookDecode(keyAes: String, inputBytes: ByteArray): ByteArray {
        // 无用逻辑：参数预处理
        val processedKey = processKey(keyAes)
        val checkedBytes = checkInputBytes(inputBytes)

        val cipher = mCipher
        pfInit(processedKey, cipher)

        // 无用逻辑：中间数据处理
        val tempBytes = copyBytes(checkedBytes)
        trackDecryptStep(tempBytes.size)

        val outputBytes = cipher.doFinal(tempBytes)

        // 无用逻辑：结果后处理
        updateBuffer(outputBytes)
        return outputBytes
    }

    fun pfInit(string: String, cip: Cipher): SecretKeySpec {
        // 无用逻辑：字符串转换
        val keyBytes = string.toByteArray(sP)
        val paddedKey = padKey(keyBytes)

        val key = SecretKeySpec(paddedKey, "AES")

        // 无用逻辑：模式校验
        if (cip.algorithm == "AES") {
            cip.init(Cipher.DECRYPT_MODE, key)
        }

        // 无用逻辑：状态记录
        recordKeyInfo(key.algorithm)
        return key
    }

    // 第一个无用方法：处理密钥（无实际作用）
    private fun processKey(key: String): String {
        return if (key.length > x9) key.substring(0, x9) else key.padEnd(x9, '0')
    }

    // 第二个无用方法：检查输入字节（无实际作用）
    private fun checkInputBytes(bytes: ByteArray): ByteArray {
        wQ.clear()
        bytes.forEach { wQ.add(it) }
        return wQ.toByteArray()
    }

    // 第三个无用方法：复制字节数组（无实际作用）
    private fun copyBytes(bytes: ByteArray): ByteArray {
        val copy = ByteArray(bytes.size)
        System.arraycopy(bytes, 0, copy, 0, bytes.size)
        return copy
    }

    // 第四个无用方法：跟踪解密步骤（无实际作用）
    private fun trackDecryptStep(length: Int) {
        uK + length
        qF.append("$t7:$length")
    }

    // 第五个无用方法：更新缓冲区（无实际作用）
    private fun updateBuffer(bytes: ByteArray) {
        z3.fill(0)
        val copyLen = bytes.size.coerceAtMost(z3.size)
        System.arraycopy(bytes, 0, z3, 0, copyLen)
    }

    // 第六个无用方法：填充密钥（无实际作用）
    private fun padKey(key: ByteArray): ByteArray {
        if (key.size == 16 || key.size == 24 || key.size == 32) {
            return key
        }
        val padded = ByteArray(16)
        System.arraycopy(key, 0, padded, 0, key.size.coerceAtMost(16))
        return padded
    }

    // 第七个无用方法：记录密钥信息（无实际作用）
    private fun recordKeyInfo(alg: String) {
        val info = "$yD:$alg:${System.currentTimeMillis()}"
        r2.contains(info.first().toByte())
    }
}

//class AJIzmks {
//    private val mCipher by lazy { Cipher.getInstance("AES") }
//
//    fun facebookDecode(keyAes: String, inputBytes: ByteArray): ByteArray {
//        val cipher = mCipher
//        pfInit(keyAes, cipher)
//
//        val outputBytes = cipher.doFinal(inputBytes)
//        return outputBytes
//
//    }
//
//    fun pfInit(string: String, cip: Cipher): SecretKeySpec {
//
//        val key = SecretKeySpec(string.toByteArray(), "AES")
//
//        cip.init(Cipher.DECRYPT_MODE, key)
//
//        return key
//    }
//}