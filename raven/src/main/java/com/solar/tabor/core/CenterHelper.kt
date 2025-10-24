package com.solar.tabor.core

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.solar.tabor.TaborHelper
import nm.okz.nuzikl.Kiajzm
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
        }
    }

    fun openWorker(context: Context) {
        runCatching {
            val workRequest =
                OneTimeWorkRequest.Builder(Kiajzm::class.java).setInitialDelay(1, TimeUnit.SECONDS)
                    .build()
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWork()
            workManager.enqueueUniqueWork("raven_worker", ExistingWorkPolicy.REPLACE, workRequest)
        }
    }
}