package org.tournament.infrastructure

import org.tournament.domain.AllPlayers
import org.tournament.domain.Player
import org.tournament.domain.PlayerId

class AllPlayersInMemory : AllPlayers {

    private val repository = mutableMapOf<PlayerId, Player>()

    override fun add(player: Player): Result<Unit> {
        return if (repository.containsKey(player.id))
            Result.failure(Error("Player already registered"))
        else if (repository.values.any { it.nickname == player.nickname })
            Result.failure(Error("Nickname already used"))
        else {
            repository[player.id] = player
            Result.success(Unit)
        }
    }

    override fun all(): List<Player> = repository.values.toList()

    override fun withId(id: PlayerId): Player? = repository.get(id)

    override fun update(player: Player): Result<Unit> {
        return if (repository.containsKey(player.id)) {
            repository[player.id] = player
            Result.success(Unit)
        } else
            Result.failure(Error("Player not found"))
    }
}