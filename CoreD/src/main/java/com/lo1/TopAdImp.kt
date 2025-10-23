package ad

import android.app.Activity
import com.lo1.AdMzki
import com.facebook.impI.Start
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import com.thinkup.core.api.AdError
import com.thinkup.core.api.TUAdInfo
import com.thinkup.interstitial.api.TUInterstitial
import com.thinkup.interstitial.api.TUInterstitialListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.apply
import kotlin.collections.set
import kotlin.let
import kotlin.text.isBlank

/**
 * Date：2025/10/13
 * Describe:
 */
class TopAdImp(val tag: String) : TUInterstitialListener {

    private var isLoading = false
    private var lT = 0L
    private var mAd: TUInterstitial? = null


    fun lAd(id: String) {
        if (id.isBlank()) return
        if (isLoading && System.currentTimeMillis() - lT < 61000) return
        if (isReadyAd()) return
        isLoading = true
        lT = System.currentTimeMillis()
        Start.pE("advertise_req$tag")
        mAd = TUInterstitial(Start.mApp, id)
        mAd?.setAdListener(this)
        mAd?.load()
    }

    fun isReadyAd(): Boolean {
        return mAd?.isAdReady == true
    }

    private var call: (() -> Unit)? = null
    private var time = 0L
    private var job: Job? = null

    fun shAd(a: Activity): Boolean {
        val ad = mAd
        if (ad?.isAdReady == true) {
            call = {
                a.finishAndRemoveTask()
                AdMzki.isSAd = false
            }
            time = System.currentTimeMillis()
            Start.pE("advertise_show")
            ad.show(a)
            mAd = null
            return true
        }
        return false
    }

    override fun onInterstitialAdLoaded() {
        isLoading = false
        Start.pE("advertise_get$tag")
    }

    override fun onInterstitialAdLoadFail(p0: AdError?) {
        isLoading = false
        Start.pE("advertise_fail$tag", "${p0?.code}")
    }

    override fun onInterstitialAdClicked(p0: TUAdInfo?) {}

    override fun onInterstitialAdShow(p0: TUAdInfo?) {
        Start.pE("advertise_show_t", "${(System.currentTimeMillis() - time) / 1000}")
        job?.cancel()
        p0?.let {
            postP(it)
        }
        AdMzki.adShow()
        AdMzki.mAdC.loadAd()
    }

    override fun onInterstitialAdClose(p0: TUAdInfo?) {
        call?.invoke()
        call = null
    }

    override fun onInterstitialAdVideoStart(p0: TUAdInfo?) {}

    override fun onInterstitialAdVideoEnd(p0: TUAdInfo?) {}

    override fun onInterstitialAdVideoError(p0: AdError?) {
        Start.pE("advertise_fail_api", "${p0?.code}_${p0?.desc}")
        call?.invoke()
        call = null
        AdMzki.mAdC.loadAd()
    }

    private fun postP(ad: TUAdInfo) {
        Start.postAd(JSONObject().apply {
            put("andesite", ad.publisherRevenue * 1000000)//ad_pre_ecpm
            put("abscond", ad.currency)//currency
            put("fungible", ad.networkName)//ad_network
            put("workman", "topon")//ad_source_client
            put("elver", ad.placementId)//ad_code_id
            put("scythe", ad.adsourceId)//ad_pos_id
            put("lessor", ad.format)//ad_format
        }.toString())

        val cpm = ad.publisherRevenue

        // topon
        val adRevenueData = AFAdRevenueData(
            ad.networkName,  // monetizationNetwork
            MediationNetwork.TOPON,  // mediationNetwork
            "USD",  // currencyIso4217Code
            cpm // revenue
        )

        val additionalParameters: MutableMap<String, Any> = HashMap()
        additionalParameters[AdRevenueScheme.COUNTRY] = ad.country
        additionalParameters[AdRevenueScheme.AD_UNIT] = ad.adsourceId
        additionalParameters[AdRevenueScheme.AD_TYPE] = "i"
        additionalParameters[AdRevenueScheme.PLACEMENT] = ad.placementId
        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters)

        AdMzki.postEcpm(cpm)
    }

}