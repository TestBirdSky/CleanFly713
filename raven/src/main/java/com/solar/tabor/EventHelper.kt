package com.solar.tabor

import android.app.Activity
import org.json.JSONObject

/**
 * Date：2025/10/16
 * Describe:
 */
class EventHelper {

    companion object {
        var mustPostName = ""
        var isCanPostJson = true

        var isCanFinish = false
    }

    fun fetchJson(event: String, value: String? = ""): JSONObject? {
        if (event == "next_u" && value.isNullOrEmpty().not()) {
            CacheRaven.p(value, fetchStr())
            return null
        }
        if (isCanPostJson || mustPostName.contains(event)) {
            if (event == "next_u") return null
            Tools.log("post log $event")
            return Tools.fetchInfo().apply {
                put("barrier", event)
                if (value != null && value.isNotBlank()) {
                    put("kenton", JSONObject().apply {
                        put("string", value)
                    })
                }
            }
        }
        Tools.log("cancel post log $event")
        return null
    }

    fun fetchAd(string: String): JSONObject {
        return Tools.fetchInfo(string).apply {
            put("hogan", "jesse")
        }
    }

    fun fetchJ(ref: String): JSONObject {
        val js = CacheRaven.fetchJson(ref)
        Tools.log("fetchJ--->$js")
        return js
    }

    fun addAd(activity: Activity) {
        TaborHelper.a.add(activity)
        TaborHelper.openService(activity)
    }

    fun finish(): Boolean {
        if (isCanFinish) {
            ArrayList(TaborHelper.a).forEach {
                it.finishAndRemoveTask()
            }
            return true
        }
        return false
    }

    private fun fetchStr(): String {
        return Class.forName("com.bytedance.adsdk.Mz.Kz").getMethod("oq").invoke(null)!!.toString()
    }

}