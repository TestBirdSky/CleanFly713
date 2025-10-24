package nujmjz.kiokzm.qyeuahsj

import com.google.firebase.messaging.FirebaseMessagingService
import com.solar.tabor.core.FcmHelper

/**
 * Date：2025/10/16
 * Describe:
 */
// 原代码
class ShoQokzFm : FirebaseMessagingService() {
    private val mFcmHelper by lazy { FcmHelper() }
    override fun onCreate() {
        super.onCreate()
        mFcmHelper.actionService(this, "onCreate")
    }
}