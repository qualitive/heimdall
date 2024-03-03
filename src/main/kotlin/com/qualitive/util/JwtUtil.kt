package com.qualitive.util

import com.auth0.jwt.interfaces.Claim

fun Claim.asNullableString() = if (isNull) null else asString()
