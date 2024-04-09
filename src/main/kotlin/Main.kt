package org.tournament

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.tournament.api.configureRouting
import org.tournament.application.AddPlayer
import org.tournament.application.UpdatePlayerScore
import org.tournament.domain.AllPlayers
import org.tournament.infrastructure.AllPlayersInMemory

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

val appModule = module {
    single<AllPlayers> { AllPlayersInMemory() }
    single { AddPlayer(get()) }
    single { UpdatePlayerScore(get()) }
}

fun Application.module() {
    configureRouting()
    configureContentNegotiation()
    configureKoin()
}

fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureKoin() {
    install(Koin) {
        modules(appModule)
    }
}