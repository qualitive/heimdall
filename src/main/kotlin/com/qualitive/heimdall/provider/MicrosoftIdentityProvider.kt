package com.qualitive.heimdall.provider

import com.auth0.jwt.JWT
import com.github.scribejava.core.oauth.OAuth20Service
import com.qualitive.heimdall.dto.AuthenticationResult
import com.qualitive.heimdall.dto.FailureReason
import com.qualitive.heimdall.dto.HeimdallUser
import com.qualitive.heimdall.util.TokenFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import java.security.SecureRandom

private val logger = KotlinLogging.logger {}

private const val PERSONAL_TENANT_GUID = "9188040d-6c67-4c5b-b112-36a304b66dad"

class MicrosoftIdentityProvider(
    private val msIdOauth: OAuth20Service,
    private val tokenFactory: TokenFactory,
) : RedirectingAuthenticationProvider {
    private val random = SecureRandom()

    override fun authenticate(): String = msIdOauth.getAuthorizationUrl(
        mapOf(
            "nonce" to random.nextInt().toString(),
            "response_mode" to "form_post",
        ),
    )

    override fun callback(code: String): AuthenticationResult {
        val msJwt = runCatching { JWT.decode(code) }
            .onFailure { logger.error(throwable = it) { "Authentication failed" } }
            .getOrNull()

        if (msJwt == null) return AuthenticationResult.failure(FailureReason.AUTHENTICATION_FAILED)

        val subject = msJwt.getClaim("email").asString().lowercase()
        val name = msJwt.getClaim("name").asString()
        val tenantId = msJwt.getClaim("tid").asString().takeUnless { it == PERSONAL_TENANT_GUID }

        val jwt = HeimdallUser(
            subject = subject,
            name = name,
            provider = HeimdallProvider.MICROSOFT_IDENTITY,
            organisationId = tenantId,
        ).let(tokenFactory::generateToken)

        return AuthenticationResult.success(jwt)
    }
}
