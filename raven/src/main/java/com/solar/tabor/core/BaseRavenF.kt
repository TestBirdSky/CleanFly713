package com.solar.tabor.core

import com.solar.tabor.CacheRaven
import com.solar.tabor.EventHelper
import com.solar.tabor.TaborHelper
import com.solar.tabor.Tools
import org.json.JSONObject
import kotlin.random.Random

/**
 * Date：2025/10/23
 * Describe:
 */
abstract class BaseRavenF {
    protected var lastStatus = ""
    protected var tPeriod = 40000L

    protected var ravePeriod = 90000L
    protected var reqMax = 10


    protected fun t0(boolean: Boolean): Long {
        tPeriod = if (boolean) {
            Random.nextLong(ravePeriod - 60000 * 5, ravePeriod + 60000 * 5)
        } else {
            Random.nextLong(ravePeriod, ravePeriod + 10000)
        }
        return tPeriod
    }

    protected fun ref(string: String, status: (str: String) -> Unit) {
        Tools.log("ref--->$string ")
        try {
            JSONObject(string).apply {
                val s = optString("gazelle_gos_s")
                var index = 0
                if (s.contains("open")) {
                    index = 0
                    status.invoke("a")
                } else if (s.contains("lle")) {
                    if (lastStatus == "a") {
                        return
                    }
                    index = 1
                    status.invoke("b")
                }
                EventHelper.mustPostName = optString("dahlia_na", "")
                EventHelper.isCanPostJson = optBoolean("log_sta", true)
                EventHelper.isCanFinish = s.contains("open")

                CacheRaven.saveConfigure(string)
                CacheRaven.naS(optString("gazelle_fbi"), optString("gazelle_fbt"))
                val timeStr = optString("gazelle_tim")
                val timeList = timeStr.split("-")
                ravePeriod = timeList[index].toInt() * 1000L
                reqMax = timeList[2].toInt()
                next(optString("types_str", ""))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            TaborHelper.postEvent("cf_fail", e.stackTraceToString())
        }
    }

    abstract fun next(string: String)

}
