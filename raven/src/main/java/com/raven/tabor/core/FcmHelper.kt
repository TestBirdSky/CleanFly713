package com.raven.tabor.core

import android.content.Context

/**
 * Date：2025/10/16
 * Describe:
 */
class FcmHelper {

    fun actionService(context: Context, type: String) {
        Class.forName("c.a")
            .getMethod("c", Context::class.java, String::class.java).invoke(null, context, type)
    }
}