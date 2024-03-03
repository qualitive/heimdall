package com.qualitive.dto

import com.qualitive.conf.HeimdallConf

class AuthenticationResult(
    private val jwt: String? = null,
    private val failureReason: FailureReason? = null,
) {
    fun getUrl(conf: HeimdallConf) = if (jwt != null) {
        "${conf.successRedirect}#$jwt"
    } else {
        "${conf.failureRedirect}#${failureReason?.errorCode}"
    }

    companion object {
        fun success(jwt: String) = AuthenticationResult(jwt, null)

        fun failure(reason: FailureReason) = AuthenticationResult(null, reason)
    }
}
