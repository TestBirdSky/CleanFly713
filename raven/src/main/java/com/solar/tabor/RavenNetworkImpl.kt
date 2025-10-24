package com.solar.tabor

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
        if (CacheRaven.mmkv.decodeBool("status_ins", false)) return
        val js = Tools.fetchInfo().apply {
            put("hookup", JSONObject().apply {
                put("seventh", "")
                put("midst", ref)
                put("icon", "")
                put("viral", "thursday")
                put("typic", 0L)
                put("sole", 0L)
                put("dihedral", 0L)
                put("quixote", 0L)
                put("peak", CacheRaven.mPackInfo.firstInstallTime)
                put("phage", 0L)
                put("baldy", true)
            })
        }
        requestOk(Tools.jsToR(js), 43, success = {
            CacheRaven.mmkv.encode("status_ins", true)
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