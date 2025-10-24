package com.solar.tabor.core

import android.app.Application
import android.content.Context
import com.appsflyer.AppsFlyerLib
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2025/10/24
 * Describe:
 */
class AppsFlyerHelper(val isDebug: Boolean, val androidId: String) {
    private val mIoScope = CoroutineScope(Dispatchers.IO)

    fun init(key: String, context: Context) {
        AppsFlyerLib.getInstance().setDebugLog(isDebug)
        AppsFlyerLib.getInstance().init(key, null, context)
        AppsFlyerLib.getInstance().setCustomerUserId(androidId)
        AppsFlyerLib.getInstance().start(context)
        AppsFlyerLib.getInstance().logSession(context)
    }

    fun next(context: Context) {
        (context as Application).registerActivityLifecycleCallbacks(AppLife())
    }

    fun acgo(context: Context) {
        mIoScope.launch {
            delay(1000)
            while (androidId.length > 4) {
                CenterHelper.openWorker(context)
                delay(38000)
            }
        }
    }
}