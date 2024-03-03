package com.qualitive.provider.api

import com.github.scribejava.core.builder.api.DefaultApi20

data class MicrosoftIdentityApi(
    private val tenant: String,
) : DefaultApi20() {
    override fun getAccessTokenEndpoint(): String = "https://login.microsoftonline.com/$tenant/oauth2/v2.0/token"

    override fun getAuthorizationBaseUrl(): String = "https://login.microsoftonline.com/$tenant/oauth2/v2.0/authorize"
}
