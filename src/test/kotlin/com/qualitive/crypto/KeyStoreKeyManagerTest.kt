package com.qualitive.crypto

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.assertDoesNotThrow
import java.security.Security
import kotlin.test.BeforeTest
import kotlin.test.Test

class KeyStoreKeyManagerTest {
    @BeforeTest
    fun setup() {
        Security.addProvider(BouncyCastleProvider())
    }

    @Test
    fun `can instantiate class`() {
        assertDoesNotThrow {
            KeyStoreKeyManager("PrivateKey", "KeyStore")
        }
    }
}
