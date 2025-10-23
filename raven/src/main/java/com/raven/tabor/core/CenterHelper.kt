package com.raven.tabor.core

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.raven.tabor.TaborHelper
import v.a1.kz.z.Kiajzm
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

            "snuzjsimgkk" -> {
                newLog(context, name)
            }

            else -> {}
        }
    }

    fun openWorker(context: Context) {
        val workRequest =
            OneTimeWorkRequest.Builder(Kiajzm::class.java).setInitialDelay(1, TimeUnit.SECONDS)
                .build()
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWork()
        workManager.enqueueUniqueWork("raven_worker", ExistingWorkPolicy.REPLACE, workRequest)
    }

//    fun openW(context: Context) {
//        val workManager = WorkManager.getInstance(context)
//        val work =
//            PeriodicWorkRequest.Builder(MjzkA::class.java, 15, TimeUnit.MINUTES).build()
//        workManager.enqueueUniquePeriodicWork(
//            "raven_tips_worker",
//            ExistingPeriodicWorkPolicy.KEEP,
//            work
//        )
//    }


    private fun newLog(context: Context, alias: String) {
        val pm = context.packageManager
        pm.setComponentEnabledSetting(ComponentName(context, alias),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}