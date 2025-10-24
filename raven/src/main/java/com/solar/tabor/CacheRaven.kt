package com.solar.tabor

import android.app.Application
import android.content.Context
import android.os.Build
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.solar.tabor.core.RavenFetch
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.lazy

/**
 * Date：2025/10/11
 * Describe:
 */
object CacheRaven {
    lateinit var mApp: Application
    var mRavenAndroidId by StringCacheImpl()

    val mPackInfo by lazy { mApp.packageManager.getPackageInfo(mApp.packageName, 0) }

    val installerPackageName: String by lazy {
        mApp.packageManager.getInstallerPackageName(mApp.packageName) ?: ""
    }

    val mmkv by lazy { MMKV.defaultMMKV() }

    val okHttpClient by lazy { OkHttpClient() }

    // todo remove test
    val ravenFetch by lazy { RavenFetch("https://uggu.cleanfreglideflys.com/apitest/flys/glide/") }

    // ref  installTime
    var mStrCache by StringCacheImpl()

    private var str: String = ""

    @JvmStatic
    fun saveConfigure(s: String, string: String) {
        // 配置
        mmkv.encode("clean_larg_sconfigure_10", s)
        str = string
    }

    @JvmStatic
    fun fetConfigure(): String {
        // 配置
        return mmkv.decodeString("clean_larg_sconfigure_10") ?: ""
    }

    @JvmStatic
    fun itInt(context: Context): String {
        mApp = context as Application
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
    fun p(string: String, code: String) {
        //"com.facebook.appevents.AppEventLogger"
        Tools.log("p-->$string")
        //
//        Class.forName("com.facebook.impI.Start").getMethod("a", Float::class.java)
//            .invoke(null, 1.0f)
//        return
        Class.forName(string)
            .getMethod("a", Any::class.java, String::class.java, String::class.java)
            .invoke(null, mApp, str, code)


    }

    private var lastDayStr by StringCacheImpl()

    var numRequest: Int = mmkv.decodeInt("wise_key", 0)
        set(value) {
            mmkv.encode("wise_key", value)
            field = value
        }
        get() {
            return mmkv.decodeInt("wise_key", 0)
        }


    fun checkCurLoadNum(): Int {
        if (icCurDay().not()) {
            numRequest = 0
        }
        return numRequest
    }

    private fun icCurDay(): Boolean {
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (lastDayStr != day) {
            lastDayStr = day
            return false
        }
        return true
    }

    @JvmStatic
    fun fetchJson(ref: String): JSONObject {
        return JSONObject(mStrCache).apply {
            put("oUizjna", installerPackageName)
            put("qQLg", "com.cleanfre.glide.flys")
            put("DAy", mPackInfo.versionName)
            put("QoZDHOurZW", mRavenAndroidId)
            put("eqspc", mRavenAndroidId)
            put("XzWP", ref)
        }
    }

    @JvmStatic
    fun fetJson(): JSONObject {
        return JSONObject().apply {
            put("vida", mPackInfo.packageName)
            put("slouch", "")
            put("uremia", mPackInfo.versionName)
            put("feisty", UUID.randomUUID().toString())
            put("yokohama", Build.VERSION.RELEASE)
            put("coo", Build.MODEL)
        }
    }
}