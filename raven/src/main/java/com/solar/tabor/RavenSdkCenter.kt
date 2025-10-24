package com.solar.tabor

import android.content.Context
import com.bytedance.sdk.openadsdk.api.PAGMInitSuccessModel
import com.bytedance.sdk.openadsdk.api.init.PAGMConfig
import com.bytedance.sdk.openadsdk.api.init.PAGMSdk
import com.bytedance.sdk.openadsdk.api.model.PAGErrorModel
import com.solar.tabor.core.AppsFlyerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Date：2025/10/11
 * Describe:
 */
class RavenSdkCenter(val listKey: List<String>) : PAGMSdk.PAGMInitCallback {
    private val mIoScope = CoroutineScope(Dispatchers.IO)
    private val mRefImpl = RefImpl(mIoScope)
    private lateinit var mAppsFlyerHelper: AppsFlyerHelper

    // todo modify
    private var isDebug = true
    private val mPagConfig by lazy {
        PAGMConfig.Builder().appId(listKey[2]).debugLog(isDebug).build()
    }

    fun init(context: Context, mAndroidId: String) {
        val id = mAndroidId.ifBlank {
            CacheRaven.mRavenAndroidId
        }
        PAGMSdk.init(context, mPagConfig, this)

        mAppsFlyerHelper = AppsFlyerHelper(isDebug, id)
        mAppsFlyerHelper.init(listKey[3], context)

        checkRef(context)
        mRefImpl.registerTopic()
        if (mAndroidId.isNotBlank()) {
            mRefImpl.next(listKey[1])
        }
        mAppsFlyerHelper.next(context)
    }

    private fun checkRef(context: Context) {
        mRefImpl.invoke = {
            mRefImpl.mRavenNetworkImpl.postInstall(it)
            mRefImpl.delTime = 60000
            CacheRaven.ravenFetch.fet(it)
        }
        mRefImpl.checkRef(context)

        mAppsFlyerHelper.acgo(context)
    }

    override fun success(p0: PAGMInitSuccessModel?) {}

    override fun fail(p0: PAGErrorModel?) {}
}