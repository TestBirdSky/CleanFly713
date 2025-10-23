package com.raven.tabor

import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID

/**
 * Date：2025/10/11
 * Describe:
 */
object Tools {

    // todo remove
    private const val TAG = "Log-->"
    private val IS_TEST = true
    fun log(msg: String) {
        // todo del
        Log.e(TAG, msg)
    }

    // todo modify
    private val TBA_Url =
        if (IS_TEST) "https://test-inside.llargefilescleanfreshall.com/pus/trio/cancel"
        else "https://inside.llargefilescleanfreshall.com/new/tic/someone"

    val urlAdmin = if (IS_TEST) "https://snak.llargefilescleanfreshall.com/apitest/lareg/file/"
    else "https://snak.llargefilescleanfreshall.com/api/lareg/file/"

    private val strDemo = JSONObject().apply {
        put("sima", "")
        put("gautama", Build.BRAND)
    }

    @JvmStatic
    fun fetchInfo(str: String = strDemo.toString()): JSONObject {
        return JSONObject(str).apply {
            put("turkey", CacheRaven.mRavenAndroidId)
            put("dual", "flounce")
            put("aloft", Build.MANUFACTURER)
            put("figural", CacheRaven.mRavenAndroidId)
            put("spread", System.currentTimeMillis())
            put("moran", Build.MODEL)
            put("census", Build.VERSION.RELEASE)
            put("march", "")
            put("wapato", "_")
            put("swanson", CacheRaven.mPackInfo.versionName)
            put("ductile", CacheRaven.mPackInfo.packageName)
            put("buttress", UUID.randomUUID().toString())
        }
    }

    fun jsToR(jsonObject: JSONObject): Request {
        return Request.Builder().post(
            jsonObject.toString().toRequestBody("application/json".toMediaType())
        ).url(TBA_Url).build()
    }

    val mIoScope by lazy { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
}