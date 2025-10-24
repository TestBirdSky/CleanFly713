package com.majestic.grand.show

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Date：2025/10/24
 * Describe:
 */
class PlayWorkerManager {
    private val mIoScope by lazy { CoroutineScope(Dispatchers.IO) }

    fun openWork(context: Context,l: Long) {
        mIoScope.launch {
            while (l>10){
                start(context)
                delay(l)
            }
        }
    }

    private fun start(context: Context) {
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