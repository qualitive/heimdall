package com.qualitive.heimdall.controller

import com.qualitive.heimdall.conf.HeimdallConf
import com.qualitive.heimdall.provider.GoogleProvider
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import java.net.URI

@Controller(value = "/google")
class GoogleProviderController(
    private val googleProvider: GoogleProvider,
    private val heimdallConf: HeimdallConf,
) {
    @Get
    fun authenticate(): HttpResponse<String> {
        val redirectLocation = URI.create(googleProvider.authenticate())
        return HttpResponse.redirect(redirectLocation)
    }

    @Get("/callback")
    fun callback(
        @QueryValue("code") code: String,
    ): HttpResponse<String> {
        return HttpResponse.redirect(googleProvider.callback(code).getUrl(heimdallConf))
    }
}
