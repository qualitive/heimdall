package com.qualitive.provider

import com.qualitive.dto.AuthenticationResult

interface RedirectingAuthenticationProvider {
    fun authenticate(): String

    fun callback(code: String): AuthenticationResult
}
