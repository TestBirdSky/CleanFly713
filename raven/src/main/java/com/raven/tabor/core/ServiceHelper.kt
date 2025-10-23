package com.raven.tabor.core

import android.app.Notification
import android.app.Notification.CATEGORY_CALL
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.raven.tabor.R

/**
 * Date：2025/10/16
 * Describe:
 */
class ServiceHelper(val id: Int, val childId: String) {

    var idType = 1
    fun create(context: Context, idIcon: Int): Notification {
        return NotificationCompat.Builder(context, childId).setAutoCancel(false).setContentText("")
            .setSmallIcon(idIcon).setOngoing(true).setOnlyAlertOnce(true).setContentTitle("")
            .setCategory(CATEGORY_CALL)
            .setCustomContentView(
                RemoteViews(
                    context.packageName,
                    if (is10B()) R.layout.raven_uz else id
                )
            )
            .build()
    }

    private fun is10B(): Boolean {
        return Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q
    }
}