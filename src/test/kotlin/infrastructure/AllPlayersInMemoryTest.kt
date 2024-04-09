package infrastructure

import org.tournament.domain.Player
import org.tournament.domain.PlayerId
import org.tournament.domain.PlayerNickname
import org.tournament.domain.PlayerScore
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
        repository.add(Player(playerId, PlayerNickname("toto"), PlayerScore(0)))

        val result = repository.add(Player(playerId, PlayerNickname("titi"), PlayerScore(10)))

        assertTrue(result.isFailure)
    }

    @Test
    fun `all should return all players`() {
        val repository = AllPlayersInMemory()
        val toto = Player.new(PlayerNickname("toto"))
        val titi = Player.new(PlayerNickname("titi"))
        repository.add(toto)
        repository.add(titi)

        val result = repository.all()

        assertEquals(listOf(toto, titi), result)
    }

    @Test
    fun `withId should return player if id exists`() {
        val repository = AllPlayersInMemory()
        val toto = Player.new(PlayerNickname("toto"))
        repository.add(toto)

        val result = repository.withId(toto.id)

        assertEquals(toto, result)
    }

    @Test
    fun `withId should return null if id does not exist`() {
        val repository = AllPlayersInMemory()

        val result = repository.withId(PlayerId.random())

        assertEquals(null, result)
    }
}