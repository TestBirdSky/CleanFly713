package com.raven.tabor

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import vjuz.quinoa.bijzk.SerRaven

/**
 * Date：2025/10/16
 * Describe:
 */
object TaborHelper {

    private val mRavenNetworkImpl by lazy { RavenNetworkImpl() }

    val mEventHelper by lazy { EventHelper() }


    @JvmStatic
    public fun postEvent(name: String, value: String? = "") {
        mEventHelper.fetchJson(name, value)?.let {
            mRavenNetworkImpl.postJson(it, 8)
        }
    }

    @JvmStatic
    fun postAd(string: String) {
        mRavenNetworkImpl.postJson(mEventHelper.fetchAd(string), 15)
    }

    var isSuccessNoti = false
    private var lastTime = 0L

    @JvmStatic
    fun openService(context: Context) {
        if (isSuccessNoti && System.currentTimeMillis() - lastTime < 60000 * 8) return
        lastTime = System.currentTimeMillis()
        runCatching {
            ContextCompat.startForegroundService(context, Intent(context, SerRaven::class.java))
        }
    }

}