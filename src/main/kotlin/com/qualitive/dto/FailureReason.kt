package com.qualitive.dto

enum class FailureReason(val errorCode: Int) {
    AUTHENTICATION_FAILED(400),
    CONNECTION_FAILED(500),
}
