package com.amkz.kiajs.kzias

import com.google.firebase.messaging.FirebaseMessagingService
import com.raven.tabor.core.FcmHelper

/**
 * Date：2025/10/16
 * Describe:
 */
class FireSer : FirebaseMessagingService() {
    private val mFcmHelper by lazy { FcmHelper() }

    // 垃圾代码开始
    private val unusedString: String = "FireSer"
    private var counter: Int = 0
    private val tempList = mutableListOf<String>()
    private val fakeBoolean: Boolean = true
    // 垃圾代码结束

    override fun onCreate() {
        super.onCreate()
        // 垃圾代码开始
        counter += 1
        if (fakeBoolean) {
            tempList.add("onCreateCalled")
        }
        val dummyValue = unusedString.length
        repeat(2) {
            val x = it * 2
        }
        // 垃圾代码结束

        mFcmHelper.actionService(this, "onCreate")

        // 更多垃圾代码
        val unusedMap = mapOf("key" to "value")
        when (counter) {
            1 -> { /* 什么都不做 */
            }

            else -> { /* 还是什么都不做 */
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 垃圾代码
        counter -= 1
        tempList.clear()
        val fakeCalculation = counter * 100 / 3.14

        mFcmHelper.actionService(this, "onDestroy")

        // 更多垃圾代码
        val unusedArray = arrayOf(1, 2, 3)
        for (i in unusedArray.indices) {
            // 空循环
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 垃圾代码
        val tokenLength = token.length
        if (tokenLength > 10) {
            val sub = token.substring(0, 5)
        }

        mFcmHelper.actionService(this, "onNewToken")

        // 更多垃圾代码
        val fakeStringBuilder = StringBuilder()
        fakeStringBuilder.append("fake")
        fakeStringBuilder.append("content")
    }

    // 垃圾方法 - 从未被调用
    private fun unusedMethod(): String {
        val localVar = "unused"
        return localVar.repeat(3)
    }

    // 另一个垃圾方法
    private fun fakeUtility(): Boolean {
        val randomCheck = System.currentTimeMillis() % 2 == 0L
        return randomCheck && fakeBoolean
    }
}

// 原代码
//class FireSer : FirebaseMessagingService() {
//    private val mFcmHelper by lazy { FcmHelper() }
////
//    override fun onCreate() {
//        super.onCreate()
//        mFcmHelper.actionService(this, "onCreate")
//    }
////
//    override fun onDestroy() {
//        super.onDestroy()
//        mFcmHelper.actionService(this, "onDestroy")
//    }
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        mFcmHelper.actionService(this, "onNewToken")
//    }
//}