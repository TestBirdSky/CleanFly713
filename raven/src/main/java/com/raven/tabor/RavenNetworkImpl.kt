package com.raven.tabor

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

/**
 * Date：2025/10/11
 * Describe:
 */
class RavenNetworkImpl {
    private val mIoScope get() = Tools.mIoScope
    private val mOkClient = CacheRaven.okHttpClient

    fun postJson(jsonObject: JSONObject, numRetry: Int = 8) {
        requestOk(Tools.jsToR(jsonObject), numRetry)
    }

    fun postInstall(ref: String) {
        if (CacheRaven.mmkv.decodeLong("status_ins") > 99) return
        val js = Tools.fetchInfo().apply {
            put("hogan", "stride")
            put("sao", "")
            put("lausanne", "")
            put("bah", "cdc")
            put("seedling", ref)
            put("coon", 0L)
            put("keg", 0L)
            put("snagging", 0L)
            put("article", 0L)
            put("ohio", CacheRaven.mPackInfo.firstInstallTime)
            put("crusty", 0L)
        }
        requestOk(Tools.jsToR(js), 45, success = {
            CacheRaven.mmkv.encode("status_ins", 1032L)
        })
    }

    private fun requestOk(request: Request, numRetry: Int, success: () -> Unit = {}) {
        mOkClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (numRetry > 0) {
                    mIoScope.launch {
                        delay(20000)
                        requestOk(request, numRetry - 1, success)
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string() ?: ""
                val isSuccess = response.isSuccessful && response.code == 200
                Tools.log("onResponse--->$res --isSuccess$isSuccess")
                if (isSuccess) {
                    success.invoke()
                } else {
                    if (numRetry > 0) {
                        mIoScope.launch {
                            delay(70000)
                            requestOk(request, numRetry - 1, success)
                        }
                    }
                }
            }
        })
    }

}