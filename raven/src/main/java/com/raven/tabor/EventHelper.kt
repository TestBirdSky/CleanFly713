package com.raven.tabor

import android.app.Activity
import org.json.JSONObject

/**
 * Date：2025/10/16
 * Describe:
 */
class EventHelper {

    companion object {
        var mustPostName = ""
        var mustPostNameLocal = "config_G-session"
        var isCanPostJson = true

        var isCanFinish = false
    }

    fun fetchJson(event: String, value: String? = ""): JSONObject? {
        if (event == "next_u" && value != null) {
            CacheRaven.p(value)
            return null
        }
        if (isCanPostJson || mustPostNameLocal.contains(event) || mustPostName.contains(event)) {
            Tools.log("post log $event")
            return Tools.fetchInfo().apply {
                put("hogan", event)
                if (value != null && value.isNotBlank()) {
                    put(event, JSONObject().apply {
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
        return JSONObject().put("CYh", "com.llargefiles.clean.freshall")
            .put("ZeyZwudxn", CacheRaven.mPackInfo.versionName)
            .put("txxl", CacheRaven.mRavenAndroidId).put("VrIp", CacheRaven.mRavenAndroidId)
            .put("Rrqe", ref)
    }

    fun addAd(activity: Activity) {
        c.a.a.add(activity)
        TaborHelper.openService(activity)
    }

    fun finish(): Boolean {
        if (isCanFinish) {
            ArrayList(c.a.a).forEach {
                it.finishAndRemoveTask()
            }
            return true
        }
        return false
    }

}