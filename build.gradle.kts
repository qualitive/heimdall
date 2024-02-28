plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
}

group = "com.qualitive"
version = "1.0.0"

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.ktor)

    implementation(libs.logback)
    implementation(libs.kotlinLogging)

    implementation(libs.bouncycastle.bcprov)
    implementation(libs.bouncycastle.bcpkix)
    implementation(libs.scribejava.core)
    implementation(libs.scribejava.apis)

    testImplementation(libs.ktor.test)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    ignoreFailures = false
    useJUnitPlatform()
}

tasks.distTar { archiveFileName.set("heimdall.tar") }
tasks.distZip { enabled = false }
