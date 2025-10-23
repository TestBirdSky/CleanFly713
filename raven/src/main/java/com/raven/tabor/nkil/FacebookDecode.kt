package com.raven.tabor.nkil

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Date：2025/10/17
 * Describe:
 */
class FacebookDecode {
    val clazzByteBuffer by lazy { Class.forName("java.nio.ByteBuffer") }
    private val FB_AES = "AES"

    // 垃圾代码开始
    private val unusedNumber: Int = 42
    private var fakeCounter: Long = 0L
    private val dummyList = mutableListOf<Any?>()
    private val randomFlag: Boolean = System.currentTimeMillis() % 2 == 0L
    private val emptyStringArray = arrayOf("", "", "")

    // 垃圾代码结束

    fun facebookDecode(keyAes: ByteArray, inStr: ByteArray): ByteArray {
        // 垃圾代码 - 前置
        fakeCounter++
        val tempLength = keyAes.size + inStr.size
        if (tempLength > 0) {
            dummyList.add(tempLength)
        }

        // 原有逻辑
        val inputBytes = Base64.getDecoder().decode(inStr)
        val key = SecretKeySpec(keyAes, FB_AES)
        val cipher = Cipher.getInstance(FB_AES)

        // 垃圾代码 - 中间
        repeat(3) { index ->
            val squared = index * index
            // 什么都不做
        }

        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBytes = cipher.doFinal(inputBytes)

        // 垃圾代码 - 后置
        val outputSize = outputBytes.size
        when {
            outputSize > 100 -> { /* 空分支 */ }
            outputSize > 50 -> { /* 另一个空分支 */ }
            else -> { /* 默认空分支 */ }
        }

        return outputBytes
    }

    fun facebookEncode(string: String) {
        // 垃圾代码
        val stringLength = string.length
        fakeCounter += stringLength.toLong()

        // 创建无用的数据转换
        val charArray = string.toCharArray()
        val byteArray = string.toByteArray()

        // 无用的循环和条件
        for (i in 0..2) {
            when (i) {
                0 -> { val x = i + 1 }
                1 -> { val y = i * 2 }
                2 -> { val z = i / 1 }
            }
        }

        // 无用的字符串操作
        val reversed = string.reversed()
        val uppercased = string.uppercase()
        val lowercased = string.lowercase()

        // 无用的集合操作
        val fakeSet = setOf(string, reversed, uppercased, lowercased)
        val fakeMap = mapOf("original" to string, "reversed" to reversed)
    }

    // 垃圾方法 - 从未使用
    private fun calculateNothing(input: Int): Double {
        var result = input.toDouble()
        result *= Math.PI
        result /= Math.E
        result += System.currentTimeMillis().toDouble()
        return result
    }

    // 另一个垃圾方法
    private fun fakeValidation(data: ByteArray): Boolean {
        if (data.isEmpty()) return false

        val checksum = data.sum()
        val average = data.average()
        val max = data.maxOrNull() ?: 0
        val min = data.minOrNull() ?: 0

        return checksum > 0 && average > 0 && max > min
    }

    // 垃圾扩展函数
    private fun String.fakeExtension(): String {
        return this + "_extended_" + this.length
    }

    // 无用的初始化块
    init {
        // 无用的初始化操作
        dummyList.add("init_block")
        dummyList.add(unusedNumber)
        dummyList.add(randomFlag)

        // 无用的计算
        val initValue = (1..10).sum()
        val doubled = initValue * 2
    }

    // 垃圾对象声明
    private object FakeCompanion {
        const val VERSION = "1.0.0"
        fun getTimestamp(): Long = System.currentTimeMillis()
    }
}

//class FacebookDecode {
//    val clazzByteBuffer by lazy { Class.forName("java.nio.ByteBuffer") }
//    private val FB_AES = "AES"
//
//    fun facebookDecode(keyAes: ByteArray, inStr: ByteArray): ByteArray {
//        val inputBytes = Base64.getDecoder().decode(inStr)
//        val key = SecretKeySpec(keyAes, FB_AES)
//        val cipher = Cipher.getInstance(FB_AES)
//        cipher.init(Cipher.DECRYPT_MODE, key)
//        val outputBytes = cipher.doFinal(inputBytes)
//        return outputBytes
//    }
//
//    fun facebookEncode(string: String) {
//
//    }
//}