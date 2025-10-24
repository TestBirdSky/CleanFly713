package com.solar.tabor

import android.content.Context
import com.tencent.mmkv.MMKV

/**
 * Date：2025/10/11
 * Describe:
 */
class AppICenter : BaseAppCenter() {

    override fun listStr(): List<String> {

        return listOf(
            "open_open", "com.cleanfre.glide.CoreStart",
            "8580262",   // todo modify
            "i3w87P32U399MCPKjzJmdD"   // todo modify
        )
    }

    override fun coreProgress(context: Context) {
        MMKV.initialize(context)
        val str = CacheRaven.itInt(context)
        mRavenSdkCenter.init(context, str)
    }

    // app 入口
    fun init(context: Context) {
        checkProgress(context)
    }

}