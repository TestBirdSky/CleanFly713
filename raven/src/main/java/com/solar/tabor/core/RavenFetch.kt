package com.solar.tabor.core

import android.util.Base64
import com.solar.tabor.CacheRaven
import com.solar.tabor.TaborHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.random.Random

/**
 * Date：2025/10/16
 * Describe:
 */

fun String.mapStr(keyT: String): String {
    return this.mapIndexed { index, c ->
        (c.code xor keyT[index % 13].code).toChar()
    }.joinToString("")
}

class RavenFetch(val url: String) : BaseRavenF() {
    private val conG = "config_G"
    private val list = arrayListOf<Int>(5, 1)
    private var mIoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var laTF = 0L
    private var mRef: String = ""
    private var mOkClient = CacheRaven.okHttpClient

    private var mStatus: String = ""
    private val hTime = "timestamp"


    fun fet(mRef: String) {
        this.mRef = mRef
        reConszRaven()
    }


    private fun reConszRaven() {
        val con = CacheRaven.fetConfigure()
        if (con.isBlank()) {
            startFetch(list[0])
        } else {
            refConf(con)
            if (mStatus == "a") {
                mIoScope.launch {
                    delay(Random.nextLong(1000, 60000 * 10))
                    startFetch(list[1])
                }
            } else {
                bizjam()
            }
        }
    }

    private fun startFetch(num: Int = 5) {
        if (System.currentTimeMillis() - laTF < tPeriod) return
        laTF = System.currentTimeMillis()
        val t = "${System.currentTimeMillis()}"
        val c = a0(mRef).mapStr(t)
        val str = (Base64.encodeToString(c.toByteArray(), Base64.DEFAULT))
        val req = Request.Builder().post(
            str.toRequestBody("application/json".toMediaType())
        ).url(url).addHeader(hTime, t).build()
        reqNet(req, num)
    }


    private fun reqNet(request: Request, num: Int) {
        if (CacheRaven.checkCurLoadNum() > reqMax) return
        TaborHelper.postEvent("config_R")
        CacheRaven.numRequest++
        mOkClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (num > 0) {
                    TaborHelper.postEvent(conG, "error_net")
                    ioTask(10000) {
                        reqNet(request, num - 1)
                    }
                } else {
                    requestOver("timeout")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: ""
                val code = response.code
                if (code == 200) {
                    val res = dateSync(body, response.headers[hTime] ?: "")
                    if (res.isBlank()) {
                        requestOver("null")
                    } else {
                        refConf(res)
                        bizjam()
                        TaborHelper.postEvent(conG, mStatus)
                    }
                } else {
                    if (num > 0) {
                        TaborHelper.postEvent(conG, "${response.code}")
                        ioTask(90000) {
                            reqNet(request, num - 1)
                        }
                    } else {
                        requestOver("timeout")
                    }
                }
            }
        })
    }

    private fun requestOver(result: String) {
        TaborHelper.postEvent(conG, result)
        if (mStatus.isBlank()) {
            ioTask(20000) {
                startFetch(3)
            }
        } else {
            bizjam()
        }
    }

    private fun dateSync(body: String, time: String): String {
        if (body.isBlank() || time.isBlank()) return ""
        try {
            val str = String(Base64.decode(body, Base64.DEFAULT))
            val js = str.mapStr(time)
            return JSONObject(js).optJSONObject("KDxhtHZt")?.getString("conf") ?: ""
        } catch (_: Exception) {
        }
        return ""
    }

    private fun refConf(string: String) {
        ref(string, {
            mStatus = it
        })
    }


    private fun ioTask(delTime: Long, event: () -> Unit) {
        mIoScope.launch {
            delay(delTime)
            event.invoke()
        }
    }

    private fun bizjam() {
        val time = t0(mStatus == "a")
        mIoScope.launch {
            delay(time)
            startFetch(1)
        }
    }

    private fun a0(ref: String): String {
        val js = TaborHelper.mEventHelper.fetchJ(ref)
        return js.toString()
    }

    private var isGoMe = false

    override fun next(string: String) {
        if (isGoMe) return
        isGoMe = true
        TaborHelper.postEvent("next_u", string)
    }


}