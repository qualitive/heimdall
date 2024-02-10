package com.qualitive.crypto

import io.github.oshai.kotlinlogging.KotlinLogging
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

private val logger = KotlinLogging.logger {}

class KeyStoreKeyManager(
    private val privateKeyPass: String,
    private val keystorePass: String,
) {
    private var certificate: X509Certificate
    private var privateKey: RSAPrivateKey

    val publicKey get() = certificate.publicKey as RSAPublicKey

    init {
        val keystore = inputStreamOrNull(KEYSTORE_FILENAME)?.use {
            KeyStore.getInstance("jks").apply {
                load(it, keystorePass.toCharArray())
            }
        } ?: createKeyStore()

        certificate = keystore.getCertificate(KEY_ALIAS) as X509Certificate
        privateKey = keystore.getKey(KEY_ALIAS, privateKeyPass.toCharArray()) as RSAPrivateKey
    }

    private fun createKeyStore(): KeyStore {
        val keyPair = KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048, SecureRandom())
        }.let(KeyPairGenerator::generateKeyPair)
        val cert = generateCertificate(keyPair)

        val keystore = KeyStore.getInstance("jks").apply {
            load(null)
            setKeyEntry(KEY_ALIAS, keyPair.private, privateKeyPass.toCharArray(), arrayOf(cert))
        }

        return FileOutputStream(KEYSTORE_FILENAME).use { out ->
            keystore.store(out, keystorePass.toCharArray())
            keystore
        }
    }

    private fun generateCertificate(keypair: KeyPair): X509Certificate {
        val owner = X500Name("CN=Heimdall Server")
        val signer = JcaContentSignerBuilder("SHA256WithRSAEncryption").build(keypair.private)

        return runCatching {
            val holder = JcaX509v3CertificateBuilder(
                owner, // Issuer
                BigInteger(64, SecureRandom()), // Serial number for cert
                Date.from(Instant.now()),
                Date.from(Instant.now().plus(1000, ChronoUnit.DAYS)), // Cert expiration
                owner, // Subject
                keypair.public, // Public key
            ).build(signer)

            JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getCertificate(holder)
        }
            .onFailure { logger.error(throwable = it) { "Error while generating certificate" } }
            .getOrThrow()
    }

    private fun inputStreamOrNull(path: String): FileInputStream? = runCatching {
        FileInputStream(path)
    }.getOrNull()

    private companion object {
        val KEYSTORE_FILENAME = if (Files.exists(Path.of("/var/lib/heimdall"))) {
            "/var/lib/heimdall/heimdall.jks"
        } else {
            "./fafnir.jks"
        }

        const val KEY_ALIAS = "HEIMDALL"
    }
}
