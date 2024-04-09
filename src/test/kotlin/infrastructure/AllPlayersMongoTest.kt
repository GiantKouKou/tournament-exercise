package infrastructure

import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.MongoDBContainer
import org.tournament.domain.*
import org.tournament.infrastructure.AllPlayersMongo
import org.tournament.infrastructure.PlayerDB
import kotlin.test.*

class AllPlayersMongoTest {

    private val mongo = MongoDBContainer("mongo:latest").apply {
        startupAttempts = 1
    }
    val mongoRunning = mongo.start()

    private val client = MongoClient.create(mongo.replicaSetUrl)
    private val database = client.getDatabase("tournament")
    private val repository = AllPlayersMongo(database)

    @BeforeTest
    fun beforeTest() = runBlocking {
        repository.clear()
    }

    @Test
    fun `add should return success if player does not exist`() = runBlocking {
        val player = Player.new(PlayerNickname("toto"))

        val addedPlayer = repository.add(player)

        assertTrue(addedPlayer.isSuccess)
        val result = database.getCollection<PlayerDB>("players").find()
        assertEquals(1, result.count())
        val playerDb = result.first()
        assertEquals("toto", playerDb.nickname)
        assertEquals(0, playerDb.score)
        assertEquals(player.id.value, playerDb.id)
        assertTrue(true)
    }

    @Test
    fun `add should return player with its rank`(): Unit = runBlocking {
        val toto = Player.new(PlayerNickname("toto"))
        toto.score = PlayerScore(10)

        val updatedToto = repository.add(toto)

        assertTrue(updatedToto.isSuccess)
        updatedToto.onSuccess {
            assertEquals(1, it.rank?.value)
        }
    }

    @Test
    fun `all should return all players ordered by rank`() = runBlocking {
        val toto = Player.new(PlayerNickname("toto"))
        toto.score = PlayerScore(5)
        val titi = Player.new(PlayerNickname("titi"))
        titi.score = PlayerScore(10)
        repository.add(toto).getOrThrow()
        repository.add(titi).getOrThrow()

        val result = repository.all()

        assertEquals(listOf(titi.id, toto.id), result.map { it.id })
    }

    @Test
    fun `withId should return player if id exists`() = runBlocking {
        val toto = Player.new(PlayerNickname("toto"))
        repository.add(toto).getOrThrow()

        val result = repository.withId(toto.id)

        assertEquals(toto.id, result?.id)
        assertEquals(PlayerRank(1), result?.rank)
    }

    @Test
    fun `withId should return null if id does not exist`() = runBlocking {
        val result = repository.withId(PlayerId.random())

        assertEquals(null, result)
    }

    @Test
    fun `update should return error if player is not found`() = runBlocking {
        val toto = Player.new(PlayerNickname("toto"))

        val result = repository.update(toto)

        assertTrue(result.isFailure)
    }

    @Test
    fun `update should return success if player exists`() = runBlocking {
        val toto = Player.new(PlayerNickname("toto"))
        repository.add(toto)

        toto.score = PlayerScore(10)
        val result = repository.update(toto)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `update should return player with updated rank`(): Unit = runBlocking {
        val toto = Player.new(PlayerNickname("toto"))
        toto.score = PlayerScore(10)
        repository.add(toto)
        val titi = Player.new(PlayerNickname("titi"))
        toto.score = PlayerScore(5)
        repository.add(titi)

        titi.score = PlayerScore(12)
        val updatedTiti = repository.update(titi)

        updatedTiti.onSuccess {
            assertEquals(1, it.rank?.value)
        }.onFailure {
            fail("should be successful")
        }
    }

    @Test
    fun `clear should remove all players`() = runBlocking {
        val toto = Player.new(PlayerNickname("toto"))
        repository.add(toto)

        repository.clear()

        assertEquals(0, repository.all().size)
    }
}