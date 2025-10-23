package com.raven.tabor

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.raven.tabor.core.CenterHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Date：2025/10/11
 * Describe:
 */
class RefImpl(val mIoScope: CoroutineScope) : BaseRefCache() {
    var delTime = 18000L

    var invoke: ((ref: String) -> Unit)? = null

    fun checkRef(context: Context) {
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
        return mTopicRegisterSuccess.ifBlank { "raven_fcm" }
    }

    override fun fetchSessionStr(): String {
        return "post"
    }


}