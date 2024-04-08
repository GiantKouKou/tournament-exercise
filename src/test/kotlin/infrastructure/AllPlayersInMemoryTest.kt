package infrastructure

import org.tournament.domain.Player
import org.tournament.domain.PlayerId
import org.tournament.domain.PlayerNickname
import org.tournament.infrastructure.AllPlayersInMemory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllPlayersInMemoryTest {

    @Test
    fun `add should return success if player does not exist`() {
        val repository = AllPlayersInMemory()

        val result = repository.add(Player(PlayerId.random(), PlayerNickname("toto")))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `add should return failure if nickname already used`() {
        val repository = AllPlayersInMemory()
        repository.add(Player(PlayerId.random(), PlayerNickname("toto")))

        val result = repository.add(Player(PlayerId.random(), PlayerNickname("toto")))

        assertTrue(result.isFailure)
    }

    @Test
    fun `add should return failure if id already registered`() {
        val repository = AllPlayersInMemory()
        val playerId = PlayerId.random()
        repository.add(Player(playerId, PlayerNickname("toto")))

        val result = repository.add(Player(playerId, PlayerNickname("titi")))

        assertTrue(result.isFailure)
    }

    @Test
    fun `all should return all players`() {
        val repository = AllPlayersInMemory()
        val toto = Player(PlayerId.random(), PlayerNickname("toto"))
        val titi = Player(PlayerId.random(), PlayerNickname("titi"))
        repository.add(toto)
        repository.add(titi)

        val result = repository.all()

        assertEquals(listOf(toto, titi), result)
    }
}