package com.solar.tabor.core

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.solar.tabor.TaborHelper
import com.majestic.grand.show.Kiajzm
import java.util.concurrent.TimeUnit

/**
 * Date：2025/10/16
 * Describe:
 */
object CenterHelper {
    var name = ""
    fun checkFcm(context: Context, string: String) {
        when (string) {
            "onCreate" -> {
                TaborHelper.openService(context)
            }
            else -> {

            }
        }
    }

    fun openWorker(context: Context) {

    }
}