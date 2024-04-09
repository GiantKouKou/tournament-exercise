package domain

import org.tournament.domain.Player
import org.tournament.domain.PlayerId
import org.tournament.domain.PlayerNickname
import org.tournament.domain.PlayerScore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class PlayerIdTest {

    @Test
    fun `should generate uuid`() {
        val playerId = PlayerId.random()

        assertNotNull(playerId.value)
        assertEquals(36, playerId.value.length)
    }
}

class PlayerScore {

    @Test
    fun `should accept positive values`() {
        val score = PlayerScore(10)

        assertEquals(10, score.value)
    }

    @Test
    fun `should reject negative values`() {
        assertFails { PlayerScore(-10) }
    }
}

class PlayerTest {

    @Test
    fun `new should create new player with given nickname`() {
        val player = Player.new(PlayerNickname("toto"))

        assertNotNull(player.id)
        assertEquals("toto", player.nickname.value)
        assertEquals(0, player.score.value)
    }

    @Test
    fun `should update score`() {
        val player = Player.new(PlayerNickname("toto"))
        player.score = PlayerScore(10)

        assertEquals(10, player.score.value)
    }
}