package com.qualitive.heimdall
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class HeimdallTest(
    private val application: EmbeddedApplication<*>,
    @Client("/") val client: HttpClient,
) : StringSpec({

        "test the server is running" {
            assert(application.isRunning)
        }

        "can get public key" {
            val result = client.toBlocking().retrieve("/public-key")
            result shouldNotBe null
        }
    })
