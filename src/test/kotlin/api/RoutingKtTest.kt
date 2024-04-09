package api

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.tournament.api.CreatePlayerApi
import org.tournament.api.UpdatePlayerScoreApi
import org.tournament.api.configureRouting
import org.tournament.configureContentNegotiation
import org.tournament.configureKoin
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingKtTest {

    @Test
    fun `get players list should return empty array`() = testApplication {
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
    fun `create player should return created response`() = testApplication {
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
            setBody(CreatePlayerApi("toto"))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertEquals("Welcome to the tournament toto!!", bodyAsText())
        }
    }

    @Test
    fun `get single player should return not found response`() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            configureKoin()
        }

        client.get("/players/1").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun `update score should return not found response`() = testApplication {
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

        client.put("/players/1/score") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePlayerScoreApi(10))
        }.apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun `clear should return no content`() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            configureKoin()
        }

        client.delete("/players").apply {
            assertEquals(HttpStatusCode.NoContent, status)
        }
    }
}