package com.raven.tabor

import android.app.Application
import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.bytedance.sdk.openadsdk.api.PAGMInitSuccessModel
import com.bytedance.sdk.openadsdk.api.init.PAGMConfig
import com.bytedance.sdk.openadsdk.api.init.PAGMSdk
import com.bytedance.sdk.openadsdk.api.model.PAGErrorModel
import com.raven.tabor.core.AppLife
import com.raven.tabor.core.CenterHelper
import com.thinkup.core.api.TUSDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2025/10/11
 * Describe:
 */
class RavenSdkCenter(val listKey: List<String>) : PAGMSdk.PAGMInitCallback {
    private val mIoScope = CoroutineScope(Dispatchers.IO)
    private val mRefImpl = RefImpl(mIoScope)

    // todo modify
    private var isDebug = true
    private val mPagConfig by lazy {
        PAGMConfig.Builder().appId(listKey[2]).debugLog(isDebug).build()
    }

    fun init(context: Context, mAndroidId: String) {
        val id = mAndroidId.ifBlank {
            CacheRaven.mRavenAndroidId
        }
        TUSDK.init(context, listKey[0], listKey[1])

        PAGMSdk.init(context, mPagConfig, this)

        AppsFlyerLib.getInstance().setDebugLog(isDebug)
        AppsFlyerLib.getInstance().init(listKey[3], null, context)
        AppsFlyerLib.getInstance().setCustomerUserId(id)
        AppsFlyerLib.getInstance().start(context)
        AppsFlyerLib.getInstance().logSession(context)

        checkRef(context)
        mRefImpl.registerTopic()
        (context as Application).registerActivityLifecycleCallbacks(AppLife())
        if (mAndroidId.isNotBlank()) {
            CenterHelper.checkFcm(context, "snuzjsimgkk")
        }
    }

    private fun checkRef(context: Context) {
        mRefImpl.invoke = {
            mRefImpl.mRavenNetworkImpl.postInstall(it)
            mRefImpl.delTime = 60000
            CacheRaven.ravenFetch.fet(it)
        }
        mRefImpl.checkRef(context)
        mIoScope.launch {
            while (mRefImpl.delTime > 1098) {
                CenterHelper.openWorker(context)
                delay(40000)
            }
        }
    }

    override fun success(p0: PAGMInitSuccessModel?) {}

    override fun fail(p0: PAGErrorModel?) {}
}