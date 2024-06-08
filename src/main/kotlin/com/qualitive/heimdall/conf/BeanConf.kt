package com.qualitive.heimdall.conf

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.qualitive.heimdall.crypto.KeyStoreKeyManager
import com.qualitive.heimdall.provider.GoogleProvider
import com.qualitive.heimdall.provider.MicrosoftIdentityProvider
import com.qualitive.heimdall.provider.api.MicrosoftIdentityApi
import com.qualitive.heimdall.util.TokenFactory
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton

@Factory
class BeanConf {
    @Context
    fun heimdallConf(
        @Value("\${heimdall.url}") url: String,
        @Value("\${heimdall.success-url}") successUrl: String,
        @Value("\${heimdall.failure-url}") failureUrl: String,
    ) = HeimdallConf(
        url = url,
        successRedirect = successUrl,
        failureRedirect = failureUrl,
    )

    @Singleton
    fun tokenFactory(keyStoreKeyManager: KeyStoreKeyManager) = TokenFactory(keyStoreKeyManager)

    @Context
    fun googleProvider(
        @Value("\${heimdall.google.app-id}") appId: String,
        @Value("\${heimdall.google.secret}") secret: String,
        heimdallConf: HeimdallConf,
        tokenFactory: TokenFactory,
    ): GoogleProvider {
        val googleOauth = ServiceBuilder(appId)
            .apiSecret(secret)
            .callback("${heimdallConf.url}/google/callback")
            .defaultScope("openid email profile")
            .build(GoogleApi20.instance())
        return GoogleProvider(googleOauth, tokenFactory)
    }

    @Context
    fun msidProvider(
        @Value("\${heimdall.msid.app-id}") appId: String,
        @Value("\${heimdall.msid.secret}") secret: String,
        @Value("\${heimdall.msid.tenant}") tenant: String,
        heimdallConf: HeimdallConf,
        tokenFactory: TokenFactory,
    ): MicrosoftIdentityProvider {
        val msidOauth = ServiceBuilder(appId)
            .apiSecret(secret)
            .callback("${heimdallConf.url}/msidentity/callback")
            .responseType("id_token")
            .defaultScope("openid email profile")
            .build(MicrosoftIdentityApi(tenant))
        return MicrosoftIdentityProvider(msidOauth, tokenFactory)
    }
}
