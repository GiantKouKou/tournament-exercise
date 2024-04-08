package api

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.koin.ktor.plugin.Koin
import org.tournament.api.PlayerApi
import org.tournament.api.configureRouting
import org.tournament.appModule
import org.tournament.configureContentNegotiation
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingKtTest {
    @Test
    fun testPlayers() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            install(Koin) {
                modules(appModule)
            }
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.get("/players").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("[]", bodyAsText())
        }
        client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerApi("toto"))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertEquals("Welcome to the tournament toto!!", bodyAsText())
        }
    }
}