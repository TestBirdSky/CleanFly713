package com.solar.tabor

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
        if (IS_TEST) "https://test-manikin.cleanfreglideflys.com/chordal/bond/furlough"
        else "https://manikin.cleanfreglideflys.com/volition/daly"

    private val strDemo = JSONObject().apply {
        put("sizjk", "")
    }

    @JvmStatic
    fun fetchInfo(str: String = strDemo.toString()): JSONObject {
        return JSONObject(str).apply {
            put("heigh", JSONObject().apply {
                put("funereal", "")
                put("mosaic", Build.BRAND)
                put("urinary", System.currentTimeMillis())
                put("digest", "scam")
                put("winslow", CacheRaven.mRavenAndroidId)
                put("allude", Build.MANUFACTURER)
                put("get", CacheRaven.mRavenAndroidId)
            })
            put("smooth", CacheRaven.fetJson())
        }
    }

    fun jsToR(jsonObject: JSONObject): Request {
        return Request.Builder().post(
            jsonObject.toString().toRequestBody("application/json".toMediaType())
        ).url(TBA_Url).build()
    }

    val mIoScope by lazy { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
}