package com.qualitive

import com.qualitive.plugins.configureRouting
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/public-key").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
