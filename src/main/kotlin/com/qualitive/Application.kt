package com.qualitive

import com.qualitive.plugins.configureRouting
import com.qualitive.plugins.configureSerialization
import io.ktor.server.application.Application
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

fun main(args: Array<String>) {
    Security.addProvider(BouncyCastleProvider())
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
