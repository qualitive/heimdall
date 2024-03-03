package com.qualitive.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.qualitive.crypto.KeyStoreKeyManager
import com.qualitive.dto.HeimdallUser
import java.time.Instant

class TokenFactory(private val keyManager: KeyStoreKeyManager) {
    fun generateToken(user: HeimdallUser): String {
        val jwt = JWT.create()
            .withIssuer("heimdall-${user.provider.value}")
            .withSubject(user.subject)
            .withIssuedAt(Instant.now())

        user.name?.let { jwt.withClaim("name", it) }
        user.metaId?.let { jwt.withClaim("mId", it) }
        user.organisationId?.let { jwt.withClaim("orgId", it) }

        val signAlgo = Algorithm.ECDSA256(keyManager.publicKey, keyManager.privateKey)
        return jwt.sign(signAlgo)
    }
}
