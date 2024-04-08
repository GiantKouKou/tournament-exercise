package org.tournament.infrastructure

import org.tournament.domain.AllPlayers
import org.tournament.domain.Player
import org.tournament.domain.PlayerNickname

class AllPlayersInMemory : AllPlayers {

    private val repository = mutableMapOf<PlayerNickname, Player>()

    override fun add(player: Player):Result<Unit> {
        return if (repository.containsKey(player.nickname))
            Result.failure(InternalError("Player already registered"))
        else {
            repository[player.nickname] = player
            Result.success(Unit)
        }
    }

    override fun all(): List<Player> = repository.values.toList()
}