package org.tournament.infrastructure

import org.tournament.domain.AllPlayers
import org.tournament.domain.Player
import org.tournament.domain.PlayerId
import org.tournament.domain.PlayerRank

class AllPlayersInMemory : AllPlayers {

    private val repository = mutableMapOf<PlayerId, Player>()

    override fun add(player: Player): Result<Player> {
        return if (repository.containsKey(player.id))
            Result.failure(Error("Player already registered"))
        else if (repository.values.any { it.nickname == player.nickname })
            Result.failure(Error("Nickname already used"))
        else {
            repository[player.id] = player
            val playerRank = this.getPlayerRank(player)
            Result.success(Player(player.id, player.nickname, player.score, playerRank))
        }
    }

    override fun all(): List<Player> =
        repository.values.sortedBy { -it.score.value }.toList().mapIndexed { index, player ->
            Player(player.id, player.nickname, player.score, PlayerRank(index + 1))
        }

    override fun withId(id: PlayerId): Player? {
        return repository.get(id)?. let {
            Player(it.id, it.nickname, it.score, getPlayerRank(it))
        }
    }

    override fun update(player: Player): Result<Player> {
        return if (repository.containsKey(player.id)) {
            repository[player.id] = player
            val playerRank = this.getPlayerRank(player)
            Result.success(Player(player.id, player.nickname, player.score, playerRank))
        } else
            Result.failure(Error("Player not found"))
    }

    private fun getPlayerRank(player: Player): PlayerRank =
        PlayerRank(repository.values.sortedBy { -it.score.value }.indexOf(player) + 1)
}