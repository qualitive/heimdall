package com.qualitive.heimdall.provider

import com.qualitive.heimdall.dto.AuthenticationResult

interface RedirectingAuthenticationProvider {
    fun authenticate(): String

    fun callback(code: String): AuthenticationResult
}
