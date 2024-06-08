package com.qualitive.heimdall.controller

import com.qualitive.heimdall.crypto.KeyStoreKeyManager
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.util.Base64

@Controller
class PublicKeyController(
    private val keyStoreKeyManager: KeyStoreKeyManager,
) {
    @Get("/public-key", produces = [MediaType.TEXT_PLAIN])
    fun getPublicKey(): String {
        return Base64.getEncoder().withoutPadding().encodeToString(keyStoreKeyManager.publicKey.encoded)
    }
}
