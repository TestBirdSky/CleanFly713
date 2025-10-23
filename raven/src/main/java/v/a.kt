package v

import android.view.View
import com.raven.tabor.TaborHelper

/**
 * Date：2025/10/16
 * Describe:
 * v.a
 */
class a : r.m {

    override fun a(string: String, value: String) {
        TaborHelper.postEvent(string, value)
    }

    override fun c(string: String) {
        TaborHelper.postAd(string)
    }

    override fun d(ref: String) {
        TaborHelper.postAd(ref)
    }

    override fun l(list: ArrayList<Long>) {
        TaborHelper.postAd(list.toString())
    }

    override fun s(view: View) {
        TaborHelper.postAd(view.transitionName)
    }

    override fun m(hashMap: HashMap<String, Int>) {
        TaborHelper.postAd(hashMap.get("b").toString())
    }
}