package com.majestic.grand.show

import android.app.Application

/**
 * Date：2025/10/24
 * Describe:
 */
class AppShow : Application() {

    override fun onCreate() {
        super.onCreate()
        PlayWorkerManager().openWork(this, 38999)
    }
}