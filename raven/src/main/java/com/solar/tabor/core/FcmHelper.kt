package com.solar.tabor.core

import android.content.Context

/**
 * Date：2025/10/16
 * Describe:
 */
class FcmHelper {

    fun actionService(context: Context, type: String) {
        CenterHelper.checkFcm(context, type)
    }
}