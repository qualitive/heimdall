package com.qualitive.plugins

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.qualitive.conf.HeimdallConf
import com.qualitive.crypto.KeyStoreKeyManager
import com.qualitive.exception.CallbackFailed
import com.qualitive.provider.GoogleProvider
import com.qualitive.provider.MicrosoftIdentityProvider
import com.qualitive.provider.api.MicrosoftIdentityApi
import com.qualitive.util.TokenFactory
import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.util.Base64

fun Application.configureRouting() {
    //region configuration
    val heimdallConf = HeimdallConf(
        url = environment.config.stringProperty("heimdall.url"),
        successRedirect = environment.config.stringProperty("heimdall.successRedirect"),
        failureRedirect = environment.config.stringProperty("heimdall.failureRedirect"),
    )

    val keyStoreKeyManager = KeyStoreKeyManager(
        privateKeyPass = environment.config.stringProperty("keystore.privateKeyPass"),
        keystorePass = environment.config.stringProperty("keystore.keystorePass"),
    )

    val tokenFactory = TokenFactory(keyStoreKeyManager)

    val googleOauth = ServiceBuilder(environment.config.stringProperty("heimdall.google.appId"))
        .apiSecret(environment.config.stringProperty("heimdall.google.secret"))
        .callback("${heimdallConf.url}/google/callback")
        .defaultScope("openid email profile")
        .build(GoogleApi20.instance())

    val msidOauth = ServiceBuilder(environment.config.stringProperty("heimdall.msid.appId"))
        .apiSecret(environment.config.stringProperty("heimdall.msid.secret"))
        .callback("${heimdallConf.url}/msidentity/callback")
        .responseType("id_token")
        .defaultScope("openid email profile")
        .build(MicrosoftIdentityApi(environment.config.stringProperty("heimdall.msid.tenant")))

    val googleProvider = GoogleProvider(googleOauth, tokenFactory)
    val msidProvider = MicrosoftIdentityProvider(msidOauth, tokenFactory)
    //endregion

    routing {
        get("/public-key") {
            val encodedPublicKey = Base64.getEncoder().withoutPadding().encodeToString(keyStoreKeyManager.publicKey.encoded)
            call.respondText(encodedPublicKey)
        }

        route("/google") {
            get {
                call.respondRedirect(googleProvider.authenticate())
            }
            get("/callback") {
                val code = call.request.queryParameters["code"] ?: throw CallbackFailed("Could not find 'code' query param")
                call.respondRedirect(googleProvider.callback(code).getUrl(heimdallConf))
            }
        }

        route("/msidentity") {
            get {
                call.respondRedirect(msidProvider.authenticate())
            }
            post("/callback") {
                val formParams = call.receiveParameters()
                val idToken = formParams["id_token"] ?: throw CallbackFailed("Could not find 'id_token' query param")
                call.respondRedirect(msidProvider.callback(idToken).getUrl(heimdallConf))
            }
        }
    }
}

private fun ApplicationConfig.stringProperty(path: String) = property(path).getString()
