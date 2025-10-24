package com.solar.tabor.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.solar.tabor.R
import com.solar.tabor.TaborHelper

/**
 * Date：2025/10/11
 * Describe:
 */
abstract class BaseRavenSer : Service() {
    private var mNotification: Notification? = null
    private val sh = ServiceHelper(R.layout.layout_th_sga, "raven_channel")

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            sh.childId, "Raven Channel", NotificationManager.IMPORTANCE_DEFAULT
        )
        (getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )
        TaborHelper.isSuccessNoti = true
        mNotification = sh.create(this, R.drawable.ic_meng_goas)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runCatching {
            startForeground(4092, mNotification)
        }
        return sh.idType
    }

    override fun onDestroy() {
        TaborHelper.isSuccessNoti = false
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }
}