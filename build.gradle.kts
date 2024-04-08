
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project

plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.9"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

group = "org.tournament"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-client-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")

    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-core")
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

application {
    mainClass.set("org.tournament.MainKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}