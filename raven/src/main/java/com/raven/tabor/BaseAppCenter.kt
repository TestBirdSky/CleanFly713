package com.raven.tabor

import android.app.ActivityManager
import android.app.Application
import android.content.Context

/**
 * Date：2025/10/11
 * Describe:
 */
abstract class BaseAppCenter {
    protected val mRavenSdkCenter by lazy { RavenSdkCenter(listStr()) }

    fun checkProgress(context: Context): String {
        val proName = context.getProName()
        if (context.packageName == proName) {
            coreProgress(context)
            return ""
        } else {
            return proName
        }
    }

    abstract fun listStr(): List<String>

    abstract fun coreProgress(context: Context)


    private fun Context.getProName(): String {
        runCatching {
            val am = getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = am.runningAppProcesses ?: return ""
            for (info in runningApps) {
                when (info.pid) {
                    android.os.Process.myPid() -> return info.processName
                }
            }
        }
        return ""
    }

}