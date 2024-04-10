package api

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.test.KoinTest
import org.testcontainers.containers.MongoDBContainer
import org.tournament.api.CreatePlayerApi
import org.tournament.api.PlayerApi
import org.tournament.api.UpdatePlayerScoreApi
import org.tournament.api.configureRouting
import org.tournament.appModule
import org.tournament.configureContentNegotiation
import org.tournament.domain.AllPlayers
import org.tournament.domain.Player
import org.tournament.domain.PlayerNickname
import org.tournament.domain.PlayerScore
import org.tournament.infrastructure.PlayerDB
import kotlin.test.assertEquals

class RoutingKtTest : KoinTest {

    fun Application.configureKoin() {
        install(Koin) {
            modules(
                module {
                    single { client }
                    single { database }
                }, appModule
            )
        }
    }


    companion object {

        private val mongo = MongoDBContainer("mongo:latest").apply {
            startupAttempts = 1
        }
        val mongoRunning = mongo.start()
        val client = MongoClient.create(mongo.replicaSetUrl)
        val database = client.getDatabase("tournament")

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            startKoin {
                modules(
                    module {
                        single { client }
                        single { database }
                    }, appModule
                )
            }
        }
    }

    @BeforeEach
    fun beforeTest(): Unit = runBlocking {
        database.getCollection<PlayerDB>("players").deleteMany(Filters.empty())

    }

    val allPlayers by inject<AllPlayers>()

    @Test
    fun `get players list should return empty array`() = testApplication {
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
        client.get("/players").apply {
            shouldHaveStatus(HttpStatusCode.OK)
            assertEquals(emptyList(), body<List<PlayerApi>>())
        }
    }

    @Test
    fun `get players list should return list with registered players ordered by score`() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            configureKoin()
        }

        val tutu = Player.new(PlayerNickname("tutu"))
        tutu.score = PlayerScore(5)
        allPlayers.add(tutu)
        val toto = Player.new(PlayerNickname("toto"))
        toto.score = PlayerScore(15)
        allPlayers.add(toto)

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.get("/players").apply {
            shouldHaveStatus(HttpStatusCode.OK)
            assertEquals(
                listOf(
                    PlayerApi(toto.id.value, "toto", 15, 1),
                    PlayerApi(tutu.id.value, "tutu", 5, 2)
                ),
                body<List<PlayerApi>>()
            )
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
    fun `get single player should return requested player`() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            configureKoin()
        }

        val player = Player.new(PlayerNickname("tutu"))
        allPlayers.add(player)

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.get("/players/${player.id.value}").apply {
            shouldHaveStatus(HttpStatusCode.OK)
            assertEquals(PlayerApi(player.id.value, "tutu", 0, 1), body<PlayerApi>())
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
    fun `update score should update player score if exists`() = testApplication {
        application {
            configureRouting()
            configureContentNegotiation()
            configureKoin()
        }

        val player = Player.new(PlayerNickname("tutu"))
        allPlayers.add(player)

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.put("/players/${player.id.value}/score") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePlayerScoreApi(10))
        }.apply {
            shouldHaveStatus(HttpStatusCode.OK)
        }
        client.get("/players/${player.id.value}") {
            contentType(ContentType.Application.Json)
        }.apply {
            shouldHaveStatus(HttpStatusCode.OK)
            assertEquals(PlayerApi(player.id.value, "tutu", 10, 1), body<PlayerApi>())
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