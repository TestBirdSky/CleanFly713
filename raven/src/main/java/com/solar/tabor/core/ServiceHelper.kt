package com.solar.tabor.core

import android.app.Notification
import android.app.Notification.CATEGORY_CALL
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

/**
 * Date：2025/10/16
 * Describe:
 */
class ServiceHelper(val id: Int, val childId: String) {
    private val listStatus = arrayListOf(true, true)

    var idType = 1
    fun create(context: Context, idIcon: Int): Notification {
        return NotificationCompat.Builder(context, childId)
            .setAutoCancel(false).setContentText("")
            .setSmallIcon(idIcon).setOngoing(listStatus[1])
            .setOnlyAlertOnce(listStatus[0])
            .setContentTitle("").setCategory(CATEGORY_CALL).setCustomContentView(
                RemoteViews(
                    context.packageName, id
                )
            ).build()
    }
}