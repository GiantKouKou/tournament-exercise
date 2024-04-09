package api

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.testcontainers.containers.MongoDBContainer
import org.tournament.api.CreatePlayerApi
import org.tournament.api.UpdatePlayerScoreApi
import org.tournament.api.configureRouting
import org.tournament.appModule
import org.tournament.configureContentNegotiation
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingKtTest {

    private val mongo = MongoDBContainer("mongo:latest").apply {
        startupAttempts = 1
    }
    val mongoRunning = mongo.start()

    fun Application.configureKoin() {
        install(Koin) {
            modules(
                module {
                    single { MongoClient.create(mongo.replicaSetUrl) }
                    single { get<MongoClient>().getDatabase("tournament") }
                }, appModule)
        }
    }

    @Test
    fun `get players list should return empty array`() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            configureKoin()
        }

        client.get("/players").apply {
            shouldHaveStatus(HttpStatusCode.OK)
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
            shouldHaveStatus(HttpStatusCode.Created)
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
            shouldHaveStatus(HttpStatusCode.NotFound)
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
            shouldHaveStatus(HttpStatusCode.NotFound)
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
            shouldHaveStatus(HttpStatusCode.NoContent)
        }
    }
}