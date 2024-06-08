plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.all.open)
    alias(libs.plugins.ksp)
    alias(libs.plugins.johnrengelman.shadow)
    alias(libs.plugins.micronaut.application)
    alias(libs.plugins.micronaut.aot)
    alias(libs.plugins.kotlinter)
}

version = "0.1"
group = "com.qualitive"

repositories {
    mavenCentral()
}

dependencies {
    ksp(libs.micronaut.http.validation)

    compileOnly(libs.micronaut.http.client)
    implementation(libs.micronaut.jackson.databind)
    implementation(libs.micronaut.kotlin.runtime)

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)

    runtimeOnly(libs.jackson.kotlin)

    runtimeOnly(libs.logback)
    implementation(libs.logstash.logbackEncoder)
    implementation(libs.kotlinLogging)

    implementation(libs.bouncycastle.bcprov)
    implementation(libs.bouncycastle.bcpkix)
    implementation(libs.scribejava.core)
    implementation(libs.scribejava.apis)
    implementation(libs.auth0.jwt)

    testImplementation(libs.micronaut.http.client)
}


application {
    mainClass = "com.qualitive.heimdall.ApplicationKt"
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
}


graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("kotest5")
    processing {
        incremental(true)
        annotations("com.qualitive.*")
    }
    aot {
    // Please review carefully the optimizations enabled below
    // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

tasks.test {
    useJUnitPlatform()
    ignoreFailures = false

    // Never use existing jks
    doFirst { delete("${project.projectDir}/heimdall.jks")}
    doLast { delete("${project.projectDir}/heimdall.jks") }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}


