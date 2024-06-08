package com.qualitive.heimdall.dto

import com.qualitive.heimdall.provider.HeimdallProvider

data class HeimdallUser(
    val subject: String,
    val provider: HeimdallProvider,
    val name: String? = null,
    val metaId: String? = null,
    val organisationId: String? = null,
)
