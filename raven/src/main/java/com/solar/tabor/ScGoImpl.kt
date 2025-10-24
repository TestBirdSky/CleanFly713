package com.solar.tabor

/**
 * Date：2025/10/23
 * Describe:
 */
class ScGoImpl : x.h {

    override fun a(string: String, value: String) {
        TaborHelper.postEvent(string, value)
    }

    override fun c(string: String) {
        TaborHelper.postAd(string)
    }

    override fun b(float: Float) {}

    override fun l(list: ArrayList<Long>) {}
}