package com.qualitive.dto

import com.qualitive.provider.HeimdallProvider

data class HeimdallUser(
    val subject: String,
    val provider: HeimdallProvider,
    val name: String? = null,
    val metaId: String? = null,
    val organisationId: String? = null,
)
