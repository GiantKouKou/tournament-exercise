package org.tournament.infrastructure

import org.tournament.domain.AllPlayers
import org.tournament.domain.Player
import org.tournament.domain.PlayerId

class AllPlayersInMemory : AllPlayers {

    private val repository = mutableMapOf<PlayerId, Player>()

    override fun add(player: Player): Result<Unit> {
        return if (repository.containsKey(player.id))
            Result.failure(InternalError("Player already registered"))
        else if (repository.values.any { it.nickname == player.nickname })
            Result.failure(InternalError("Nickname already used"))
        else {
            repository[player.id] = player
            Result.success(Unit)
        }
    }

    override fun all(): List<Player> = repository.values.toList()
}