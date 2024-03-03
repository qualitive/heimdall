package com.qualitive

import com.qualitive.plugins.configureRouting
import com.qualitive.plugins.configureSerialization
import io.ktor.server.application.Application
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Security.addProvider(BouncyCastleProvider())

    configureSerialization()
    configureRouting()
}
