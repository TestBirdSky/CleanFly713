package com.raven.tabor.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.raven.tabor.TaborHelper
import com.raven.tabor.Tools

/**
 * Date：2025/10/16
 * Describe:
 */
class AppLife : Application.ActivityLifecycleCallbacks {
    private var num = 0

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        Tools.log("onActivityCreated-->$p0")
        TaborHelper.mEventHelper.addAd(p0)
    }

    override fun onActivityDestroyed(p0: Activity) {
        Tools.log("onActivityDestroyed-->$p0")
        c.a.a.remove(p0)
    }

    override fun onActivityPaused(p0: Activity) {
        Tools.log("onActivityPaused-->$p0")

    }

    override fun onActivityResumed(p0: Activity) {
        Tools.log("onActivityResumed-->$p0")
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityStarted(p0: Activity) {
        num++
    }

    override fun onActivityStopped(p0: Activity) {
        Tools.log("onActivityStopped-->$p0")
        num--
        if (num <= 0) {
            num = 0
            c.a.b()
        }
    }
}