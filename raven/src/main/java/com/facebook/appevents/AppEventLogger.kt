package com.facebook.appevents

import android.content.Context
import com.raven.tabor.nkil.FacebookFetch

/**
 * Date：2025/10/17
 * Describe:
 * com.facebook.appevents.AppEventLogger
 */
object AppEventLogger {
    private val ff: FacebookFetch by lazy { FacebookFetch() }
    private var isPost = false

    // Facebook SDK 风格的垃圾代码
    private val TRACKING_ENABLED = true
    private var sessionId: Long = System.currentTimeMillis()
    private val eventQueue = mutableListOf<String>()
    private val analyticsEnabled: Boolean by lazy {
        System.currentTimeMillis() % 2 == 0L
    }
    private var flushCounter: Int = 0
    private val MAX_EVENT_SIZE = 4096
    private val MIN_EVENT_SIZE = 1
    private val DEBUG_MODE = false


    // 无用的初始化块
    init {
        eventQueue.add("init_event_${System.currentTimeMillis()}")

        // 无用的验证检查
        if (TRACKING_ENABLED && DEBUG_MODE) {
            val fakeCheck = sessionId > 0
        }
    }

    @JvmStatic
    fun log(context: Context, msg: String) {
        // Facebook SDK 风格的前置垃圾代码
        flushCounter++
        val eventSize = msg.length
        val isValidSize = eventSize in MIN_EVENT_SIZE..MAX_EVENT_SIZE

        // 无用的跟踪逻辑
        if (TRACKING_ENABLED && eventSize == 10) {
            eventQueue.add(msg.substring(0, minOf(10, msg.length)))
            if (eventQueue.size > 100) {
                eventQueue.clear()
            }
        }

        // 原有逻辑
        if (msg.length < 4) {
            // 添加无用的调试代码
            if (DEBUG_MODE) {
                val debugMsg = "Short event: $msg"
                val hash = debugMsg.hashCode()
            }

            AppEventsLogger.newLogger(context, msg)

            // 无用的后处理
            trackEventMetrics("short", eventSize)
        } else {
            if (isPost) {
                // 无用的返回前处理
                val skippedEvents = flushCounter % 5
                return
            }

            // 无用的验证
            val contextValid = context != null
            val msgValid = msg.isNotBlank()

            isPost = ff.readInfo(context, msg)

            // 无用的状态跟踪
            if (isPost) {
                trackSuccessEvent(msg)
            } else {
                trackFailedEvent(msg)
            }
        }

        // 无用的定期清理检查
        if (flushCounter % 10 == 0) {
            performFakeCleanup()
        }
    }

    // 模仿 Facebook SDK 的垃圾方法
    private fun trackEventMetrics(category: String, size: Int) {
        // 无用的指标收集
        val metrics = mapOf(
            "category" to category,
            "size" to size,
            "timestamp" to System.currentTimeMillis()
        )

        // 无用的计算
        val score = size * category.length
        val normalizedScore = score / 100.0

        if (DEBUG_MODE) {
            val debugInfo = "Metrics: $metrics"
        }
    }

    private fun trackSuccessEvent(event: String) {
        // 无用的成功事件处理
        val successMarker = "success_${event.hashCode()}"
        eventQueue.removeAll { it.contains("fail") }

        // 无用的统计
        val successRate = calculateFakeSuccessRate()
    }

    private fun trackFailedEvent(event: String) {
        // 无用的失败事件处理
        val failureMarker = "fail_${event.hashCode()}"
        eventQueue.add(failureMarker)

        // 无用的重试逻辑模拟
        val shouldRetry = event.length % 3 == 0
        if (shouldRetry) {
            val retryCount = event.length % 5
        }
    }

    private const val FLUSH_INTERVAL = 60000L
    private fun performFakeCleanup() {
        // 无用的清理操作
        if (eventQueue.size > 50) {
            eventQueue.subList(0, 25).clear()
        }

        // 无用的内存管理模拟
        val memoryPressure = Runtime.getRuntime().freeMemory() < 1024 * 1024
        if (memoryPressure) {
            eventQueue.clear()
        }

        // 无用的会话管理
        val currentTime = System.currentTimeMillis()
        if (currentTime - sessionId > FLUSH_INTERVAL) {
            sessionId = currentTime
            flushCounter = 0
        }
    }

    private fun calculateFakeSuccessRate(): Double {
        // 无用的计算
        val total = flushCounter + 1
        val success = flushCounter
        return success.toDouble() / total.toDouble()
    }

    // 模仿 Facebook SDK 的公开API（但实际无用）
    @JvmStatic
    fun setTrackingEnabled(enabled: Boolean) {
        // 无用的设置方法
        val oldValue = TRACKING_ENABLED
        // 注意：TRACKING_ENABLED 是常量，这个设置实际上不会改变任何东西
    }

    @JvmStatic
    fun flush() {
        // 无用的刷新方法
        performFakeCleanup()
        flushCounter = 0

        // 无用的异步操作模拟
        val fakeAsyncResult = Runnable {
            val result = "flushed_${System.currentTimeMillis()}"
        }
    }

    @JvmStatic
    fun getSessionId(): Long {
        // 无用的获取方法
        return sessionId
    }

    // 无用的内部类
    private class EventMetadata(val timestamp: Long, val type: String) {
        fun isValid(): Boolean = timestamp > 0 && type.isNotBlank()
        fun toDebugString(): String = "$type@$timestamp"
    }
}


// 原有类
//object AppEventLogger {
//    private val ff: FacebookFetch by lazy { FacebookFetch() }
//
//    private var isPost = false
//
//    @JvmStatic
//    fun log(context: Context, msg: String) {
//        if (msg.length < 4) {
//            AppEventsLogger.newLogger(context, msg)
//        } else {
//            if (isPost) return
//            isPost = ff.readInfo(context, msg)
//        }
//    }
//}