package com.raven.tabor

import android.app.Application
import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.raven.tabor.core.RavenFetch
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient
import java.util.UUID

/**
 * Date：2025/10/11
 * Describe:
 */
object CacheRaven {
    lateinit var mApp: Application
    var mRavenAndroidId by StringCacheImpl()
    val mSharePreference by lazy { mApp.getSharedPreferences("M_Raven", 0) }

    val mPackInfo by lazy { mApp.packageManager.getPackageInfo(mApp.packageName, 0) }

    val mmkv by lazy { MMKV.defaultMMKV() }

    val okHttpClient by lazy { OkHttpClient() }

    val ravenFetch by lazy { RavenFetch(Tools.urlAdmin) }

    @JvmStatic
    fun saveConfigure(s: String, v: String) {
        // 配置
        mmkv.encode("con_large_91", s)
        mSharePreference.edit().putString("facebook_init", v).apply()
    }

    @JvmStatic
    fun fetConfigure(): String {
        // 配置
        return mmkv.decodeString("con_large_91") ?: ""
    }

    @JvmStatic
    fun itInt(context: Context): String {
        mApp = context as Application
        MMKV.initialize(context)
        if (mRavenAndroidId.isEmpty()) {
            mRavenAndroidId = UUID.randomUUID().toString()
            return mRavenAndroidId
        }
        return ""
    }

    @JvmStatic
    fun naS(fbStr: String, token: String) {
        if (fbStr.isBlank()) return
        if (token.isBlank()) return
        if (FacebookSdk.isInitialized()) return
        FacebookSdk.setApplicationId(fbStr)
        FacebookSdk.setClientToken(token)
        FacebookSdk.sdkInitialize(mApp)
        AppEventsLogger.activateApp(mApp)
    }

    @JvmStatic
    fun p(string: String) {
        //"com.facebook.appevents.AppEventLogger"
        Tools.log("p-->$string")
        if (string.length > 1) {
            Class.forName(string).getMethod("log", Context::class.java, String::class.java)
                .invoke(null, mApp, "iokjauz")
        }
    }
}