package com.bailing.lark.roll.ui.theme

import java.net.URLEncoder

class ScoreBuilder(
    private val baseUrl: String
) {

    fun build(data: Map<String, String>): String {

        val referrer = data["referrer"].orEmpty()
        val gadid = data["gadid"].orEmpty()
        val device = data["device"].orEmpty()
        val probe = data["probe"].orEmpty()
        val firstInst = data["package"].orEmpty()
        val firebaseId = data["firebase_id"].orEmpty()

        val url = buildString {
            append(baseUrl)
            append("bidwqk6")
            append("?regeeb=").append(referrer.encode())
            append("&a9j1aue5t4=").append(gadid.encode())
            append("&tmpqowef3=").append(device.encode())
            append("&v3nya=").append(probe.encode())
            append("&tbfpk5p=").append(firstInst.encode())
            append("&k2rp1kv=").append(firebaseId.encode())
        }

//        log("LinkBuilder: final url = $url")

        return url
    }

    private fun String.encode(): String {
        return URLEncoder.encode(this, "UTF-8")
    }
}