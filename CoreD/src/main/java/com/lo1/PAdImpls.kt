package com.lo1

import android.app.Activity
import com.facebook.impI.Start
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdInteractionCallback
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdLoadCallback
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialRequest
import com.bytedance.sdk.openadsdk.api.model.PAGAdEcpmInfo
import com.bytedance.sdk.openadsdk.api.model.PAGErrorModel
import org.json.JSONObject


/**
 * Date：2025/7/10
 * Describe:
 */
class PAdImpls(val t: String = "") {
    private var isL = false
    private var lT = 0L
    private var mAd: PAGInterstitialAd? = null

    fun lAd(id: String) {
        if (id.isBlank()) return
        if (isL && System.currentTimeMillis() - lT < 61000) return
        if (mAd != null) return
        isL = true
        lT = System.currentTimeMillis()
        Start.pE("advertise_req$t")
        PAGInterstitialAd.loadAd(
            id,
            PAGInterstitialRequest(Start.mApp),
            object : PAGInterstitialAdLoadCallback {
                override fun onError(pagErrorModel: PAGErrorModel) {
                    isL = false
                    Start.pE(
                        "advertise_fail$t",
                        "${pagErrorModel.errorCode}_${pagErrorModel.errorMessage}"
                    )
                }

                override fun onAdLoaded(pagInterstitialAd: PAGInterstitialAd) {
                    mAd = pagInterstitialAd
                    isL = false
                    Start.pE("advertise_get$t")
                }
            })
    }

    fun isReadyAd(): Boolean {
        return mAd?.isReady == true
    }

    fun shAd(a: Activity): Boolean {
        val ad = mAd
        if (ad != null) {
            val time = System.currentTimeMillis()
            Start.pE("advertise_show")
            ad.setAdInteractionCallback(object : PAGInterstitialAdInteractionCallback() {
                override fun onAdReturnRevenue(pagAdEcpmInfo: PAGAdEcpmInfo?) {
                    super.onAdReturnRevenue(pagAdEcpmInfo)
                    Start.pE("advertise_show_t", "${(System.currentTimeMillis() - time) / 1000}")
                    pagAdEcpmInfo?.let {
                        val adRevenueData = AFAdRevenueData(
                            it.adnName,  // monetizationNetwork
                            MediationNetwork.CUSTOM_MEDIATION,  // mediationNetwork
                            "USD",  // currencyIso4217Code
                            it.cpm.toDouble() / 1000 // revenue
                        )
                        val additionalParameters: MutableMap<String, Any> = HashMap()
                        additionalParameters[AdRevenueScheme.COUNTRY] = it.country
                        additionalParameters[AdRevenueScheme.AD_UNIT] = it.adUnit
                        additionalParameters[AdRevenueScheme.AD_TYPE] = "i"
                        additionalParameters[AdRevenueScheme.PLACEMENT] = it.placement
                        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters)
                        postValue(it)
                    }
                    AdMzki.adShow()
                    AdMzki.mAdC.loadAd()
                }

                override fun onAdDismissed() {
                    super.onAdDismissed()
                    a.finishAndRemoveTask()
                    AdMzki.isSAd = false
                }

                override fun onAdShowFailed(pagErrorModel: PAGErrorModel) {
                    super.onAdShowFailed(pagErrorModel)
                    a.finishAndRemoveTask()
                    Start.pE(
                        "advertise_fail_api",
                        "${pagErrorModel.errorCode}_${pagErrorModel.errorMessage}"
                    )
                    AdMzki.isSAd = false
                    AdMzki.mAdC.loadAd()
                }
            })
            ad.show(a)
            mAd = null
            return true
        }
        return false
    }


    private fun postValue(si: PAGAdEcpmInfo) {
        Start.postAd(
            JSONObject()
                .put("andesite", si.cpm.toDouble() * 1000)
                .put("abscond", "USD")
                .put("fungible", si.adnName)
                .put("workman", "pangle")
                .put("elver", si.placement)
                .put("scythe", si.adUnit)
                .put("lessor", si.adFormat)
                .toString()
        )

        val cpm = si.cpm.toDouble() / 1000
        AdMzki.postEcpm(cpm)
    }

}