package com.lo1

import ad.TopAdImp
import android.app.Activity
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.facebook.impI.Start
import opm.z.cd
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Date：2025/7/16
 * Describe:
 */
class NAmj {
    private val mPAH = PAdImpls()// 高价值
    private val mPAdImpls = PAdImpls("1") // 低价值
    private val mToponAdH = TopAdImp("")
    private val mToponAdL = TopAdImp("1")

    private var idH = ""
    private var idL = ""
    private var isTopon = false

    //
    fun setAdId(high: String, lowId: String) {
        // id长度等于9则为pangle的ID，需要告诉测试配置错误id长度不能变
        isTopon = high.length != 9
        idH = high
        idL = lowId
    }

    fun loadAd() {
        if (isTopon) {
            mToponAdH.lAd(idH)
            mToponAdL.lAd(idL)
        } else {
            mPAH.lAd(idH)
            mPAdImpls.lAd(idL)
        }
    }

    private var job: Job? = null
    fun sa(ac: Activity) {
        AdMzki.sNumJump(0)
        if (ac is AppCompatActivity) {
            ac.onBackPressedDispatcher.addCallback {}
            job?.cancel()
            job = ac.lifecycleScope.launch {
                Start.pE("ad_done")
                delay(Random.nextLong(AdMzki.gDTime()))
                if (AdMzki.isLoadH) {
                    cd.b(ac)
                }
                var isS = false
                if (isTopon) {
                    isS = mToponAdH.shAd(ac)
                    if (isS.not()) {
                        isS = mToponAdL.shAd(ac)
                    }
                } else {
                    isS = mPAH.shAd(ac)
                    if (isS.not()) {
                        isS = mPAdImpls.shAd(ac)
                    }
                }
                if (isS.not()) {
                    delay(500)
                    ac.finishAndRemoveTask()
                }
            }
        }
    }
}