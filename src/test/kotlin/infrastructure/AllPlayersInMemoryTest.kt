package infrastructure

import org.tournament.domain.*
import org.tournament.infrastructure.AllPlayersInMemory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllPlayersInMemoryTest {

    @Test
    fun `add should return success if player does not exist`() {
        val repository = AllPlayersInMemory()

        val result = repository.add(Player.new(PlayerNickname("toto")))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `add should return player with its rank`() {
        val repository = AllPlayersInMemory()
        val toto = Player.new(PlayerNickname("toto"))
        toto.score = PlayerScore(10)

        val updatedToto = repository.add(toto)

        assertTrue(updatedToto.isSuccess)
        updatedToto.onSuccess {
            assertEquals(1, it.rank?.value)
        }
    }

    @Test
    fun `add should return failure if nickname already used`() {
        val repository = AllPlayersInMemory()
        repository.add(Player.new(PlayerNickname("toto")))

        val result = repository.add(Player.new(PlayerNickname("toto")))

        assertTrue(result.isFailure)
    }

    @Test
    fun `add should return failure if id already registered`() {
        val repository = AllPlayersInMemory()
        val playerId = PlayerId.random()
        repository.add(Player(playerId, PlayerNickname("toto"), PlayerScore(0), null))

        val result = repository.add(Player(playerId, PlayerNickname("titi"), PlayerScore(10), null))

        assertTrue(result.isFailure)
    }

    @Test
    fun `all should return all players ordered by rank`() {
        val repository = AllPlayersInMemory()
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
    fun `withId should return player if id exists`() {
        val repository = AllPlayersInMemory()
        val toto = Player.new(PlayerNickname("toto"))
        repository.add(toto).getOrThrow()

        val result = repository.withId(toto.id)

        assertEquals(toto.id, result?.id)
        assertEquals(PlayerRank(1), result?.rank)
    }

    @Test
    fun `withId should return null if id does not exist`() {
        val repository = AllPlayersInMemory()

        val result = repository.withId(PlayerId.random())

        assertEquals(null, result)
    }

    @Test
    fun `update should return error if player is not found`() {
        val repository = AllPlayersInMemory()
        val toto = Player.new(PlayerNickname("toto"))

        val result = repository.update(toto)

        assertTrue(result.isFailure)
    }

    @Test
    fun `update should return success if player exists`() {
        val repository = AllPlayersInMemory()
        val toto = Player.new(PlayerNickname("toto"))
        repository.add(toto)

        toto.score = PlayerScore(10)
        val result = repository.update(toto)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `update should return player with updated rank`() {
        val repository = AllPlayersInMemory()
        val toto = Player.new(PlayerNickname("toto"))
        toto.score = PlayerScore(10)
        repository.add(toto)
        val titi = Player.new(PlayerNickname("titi"))
        toto.score = PlayerScore(5)
        repository.add(titi)

        titi.score = PlayerScore(12)
        val updatedTiti = repository.update(titi)

        assertTrue(updatedTiti.isSuccess)
        updatedTiti.onSuccess {
            assertEquals(1, it.rank?.value)
        }
    }
}