package com.qualitive.heimdall.util

import com.auth0.jwt.interfaces.Claim

fun Claim.asNullableString() = if (isNull) null else asString()
