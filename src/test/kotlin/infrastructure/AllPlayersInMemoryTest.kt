package infrastructure

import org.tournament.domain.Player
import org.tournament.domain.PlayerNickname
import org.tournament.infrastructure.AllPlayersInMemory
import kotlin.test.*

class AllPlayersInMemoryTest {

    @Test
    fun `add should return success if player does not exist`() {
        val repository = AllPlayersInMemory()

        val result = repository.add(Player(PlayerNickname("toto")))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `add should return failure if player already exist`() {
        val repository = AllPlayersInMemory()
        repository.add(Player(PlayerNickname("toto")))

        val result = repository.add(Player(PlayerNickname("toto")))

        assertFalse(result.isFailure)
    }

    @Test
    fun `all should return all players`() {
        val repository = AllPlayersInMemory()
        val toto = Player(PlayerNickname("toto"))
        val titi = Player(PlayerNickname("titi"))
        repository.add(toto)
        repository.add(titi)

        val result = repository.all()

        assertEquals(listOf(toto, titi), result)
    }
}