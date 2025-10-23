package com.raven.tabor

import android.app.Application
import android.content.Context
import android.os.Build
import android.webkit.WebView
import com.raven.tabor.core.CenterHelper

/**
 * Date：2025/10/11
 * Describe:
 */
class AppICenter : BaseAppCenter() {

    override fun listStr(): List<String> {
        // todo modify
        return listOf(
            "h670e13c4e3ab6",
            "ac360a993a659579a11f6df50b9e78639",
            "8580262",
            "i3w87P32U399MCPKjzJmdD"
        )
    }

    override fun coreProgress(context: Context) {
        val str = CacheRaven.itInt(context)
        if (str.isNotBlank()) {
            CenterHelper.name = "gefiles.clean.freshall.StyleClean"
        } else {
            CenterHelper.name = "StyleClean"
        }
        mRavenSdkCenter.init(context, str)
    }

    // app 入口
    fun init(context: Context) {
        c.a.d = context as Application?
        val name = checkProgress(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && name.isNotBlank()) {
            runCatching {
                val processName: String = Application.getProcessName()
                if (processName.isNotBlank()) {
                    WebView.setDataDirectorySuffix(processName)
                }
            }
        }
    }

}