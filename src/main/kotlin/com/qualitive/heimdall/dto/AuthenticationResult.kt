package com.qualitive.heimdall.dto

import com.qualitive.heimdall.conf.HeimdallConf
import java.net.URI

class AuthenticationResult(
    private val jwt: String? = null,
    private val failureReason: FailureReason? = null,
) {
    fun getUrl(conf: HeimdallConf) = if (jwt != null) {
        URI.create("${conf.successRedirect}#$jwt")
    } else {
        URI.create("${conf.failureRedirect}#${failureReason?.errorCode}")
    }

    companion object {
        fun success(jwt: String) = AuthenticationResult(jwt, null)

        fun failure(reason: FailureReason) = AuthenticationResult(null, reason)
    }
}
