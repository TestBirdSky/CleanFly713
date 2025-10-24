package com.solar.tabor

import android.app.Application
import android.content.Context
import z.m

/**
 * Date：2025/10/11
 * Describe:
 */
abstract class BaseAppCenter {
    protected val mRavenSdkCenter by lazy { RavenSdkCenter(listStr()) }

    fun checkProgress(context: Context): String {
        m.d = context as Application?
        coreProgress(context)
        return ""
    }

    abstract fun listStr(): List<String>

    abstract fun coreProgress(context: Context)

}