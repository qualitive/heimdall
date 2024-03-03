package com.qualitive.provider

import com.auth0.jwt.JWT
import com.github.scribejava.apis.openid.OpenIdOAuth2AccessToken
import com.github.scribejava.core.oauth.OAuth20Service
import com.qualitive.dto.AuthenticationResult
import com.qualitive.dto.FailureReason
import com.qualitive.dto.HeimdallUser
import com.qualitive.util.TokenFactory
import com.qualitive.util.asNullableString
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class GoogleProvider(
    private val googleOauth: OAuth20Service,
    private val tokenFactory: TokenFactory,
) : RedirectingAuthenticationProvider {
    override fun authenticate(): String = googleOauth.authorizationUrl

    override fun callback(code: String): AuthenticationResult {
        val token = runCatching { googleOauth.getAccessToken(code) }
            .onFailure { logger.error(throwable = it) { "Authentication failed" } }
            .getOrNull()

        if (token == null) return AuthenticationResult.failure(FailureReason.AUTHENTICATION_FAILED)

        val googleJwt = JWT.decode((token as OpenIdOAuth2AccessToken).openIdToken)
        val subject = googleJwt.getClaim("email").asString().lowercase()
        val name = googleJwt.getClaim("name").asString()
        val orgId = googleJwt.getClaim("hd").asNullableString()

        val jwt = HeimdallUser(
            subject = subject,
            name = name,
            provider = HeimdallProvider.GOOGLE,
            organisationId = orgId,
        ).let(tokenFactory::generateToken)

        return AuthenticationResult.success(jwt)
    }
}
