package com.kijz.laisk.kzma

import android.annotation.SuppressLint
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent

/**
 * Date：2025/10/16
 * Describe:
 */
@SuppressLint("SpecifyJobSchedulerIdRange")
class AsuijJozk : JobService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}