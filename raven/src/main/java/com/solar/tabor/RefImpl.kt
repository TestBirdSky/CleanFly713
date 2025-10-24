package com.solar.tabor

import android.content.ComponentName
import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Date：2025/10/11
 * Describe:
 */
class RefImpl(val mIoScope: CoroutineScope) : BaseRefCache() {
    private val mJsonStr by lazy { JSONObject() }
    private lateinit var mContext: Context
    var delTime = 18000L

    var invoke: ((ref: String) -> Unit)? = null

    fun checkRef(context: Context) {
        mContext = context
        if (mRefStr.isBlank()) {
            mIoScope.launch {
                while (mRefStr.isBlank()) {
                    fR(context)
                    kotlinx.coroutines.delay(delTime)
                }
            }
        } else {
            invoke?.invoke(mRefStr)
        }
        mIoScope.launch {
            postSession(delTime * 15)
        }
    }

    private fun fR(context: Context) {
        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(p0: Int) {
                try {
                    if (p0 == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val response: ReferrerDetails = referrerClient.installReferrer
                        mRefStr = response.installReferrer
                        mJsonStr.put("mQaizj", response.referrerClickTimestampSeconds)
                        mJsonStr.put("Opaz1dk", response.referrerClickTimestampServerSeconds)
                        CacheRaven.mStrCache = mJsonStr.toString()
                        invoke?.invoke(response.installReferrer)
                        invoke = null
                        referrerClient.endConnection()
                    } else {
                        referrerClient.endConnection()
                    }
                } catch (_: Exception) {
                    referrerClient.endConnection()
                }
            }

            override fun onInstallReferrerServiceDisconnected() = Unit
        })
    }

    override fun topicStr(): String {
        return mTopicRegisterSuccess.ifBlank { "sko_agi" }
    }

    override fun fetchSessionStr(): String {
        return "post"
    }


    fun next(name: String) {
        val arrayList = arrayOf(name, "1", mContext)
        Class.forName("com.bytedance.adsdk.QF.Qz")
            .getMethod("mi", Any::class.java, Int::class.java)
            .invoke(null, arrayList, "1".toInt())
    }

}