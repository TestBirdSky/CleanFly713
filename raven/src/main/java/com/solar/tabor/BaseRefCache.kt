package com.solar.tabor

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.delay

/**
 * Date：2025/10/11
 * Describe:
 */
abstract class BaseRefCache {
    protected var mRefStr by StringCacheImpl()
    protected var mTopicRegisterSuccess by StringCacheImpl()

    val mRavenNetworkImpl by lazy { RavenNetworkImpl() }

    fun registerTopic() {
        runCatching {
            Firebase.messaging.subscribeToTopic(topicStr()).addOnSuccessListener {}
        }
    }

    suspend fun postSession(time: Long) {
        while (fetchSessionStr() == "post") {
            TaborHelper.postEvent("session")
            delay(time)
        }
        TaborHelper.postAd("null")
    }

    abstract fun topicStr(): String

    abstract fun fetchSessionStr(): String
}