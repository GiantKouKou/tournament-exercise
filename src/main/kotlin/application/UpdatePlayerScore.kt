package org.tournament.application

import org.tournament.domain.AllPlayers
import org.tournament.domain.Player
import org.tournament.domain.PlayerId
import org.tournament.domain.PlayerScore

class UpdatePlayerScore(private val allPlayers: AllPlayers) {

    suspend fun run(playerId: PlayerId, playerScore: PlayerScore): Result<Player> {
        val player = allPlayers.withId(playerId)
        return player?.let {
            player.score = playerScore
            allPlayers.update(player)
            return Result.success(player)
        } ?: return Result.failure(Error("Player not found"))
    }
}