package api

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.tournament.api.PlayerApi
import org.tournament.api.configureRouting
import org.tournament.configureContentNegotiation
import org.tournament.configureKoin
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingKtTest {

    @Test
    fun `should return empty array`() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            configureKoin()
        }

        client.get("/players").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("[]", bodyAsText())
        }
    }

    @Test
    fun `should return created response`() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            configureKoin()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerApi("toto"))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertEquals("Welcome to the tournament toto!!", bodyAsText())
        }
    }

    @Test
    fun `should return not found response`() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            configureKoin()
        }

        client.get("/players/1").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }
}