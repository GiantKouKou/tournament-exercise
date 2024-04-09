package org.tournament.infrastructure

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.tournament.domain.AllPlayers
import org.tournament.domain.Player
import org.tournament.domain.PlayerId
import org.tournament.domain.PlayerRank

data class PlayerDB(@BsonId val id: String, val nickname: String, val score: Int)

class AllPlayersMongo(mongoDatabase: MongoDatabase) : AllPlayers {

    companion object {
        const val PLAYERS_COLLECTION = "players"
    }

    private val database = mongoDatabase.getCollection<PlayerDB>(PLAYERS_COLLECTION)

    override suspend fun add(player: Player): Result<Player> {
        val result = database.insertOne(
            PlayerDB(player.id.value, player.nickname.value, player.score.value)
        )
        return result.insertedId?.let {
            val playerRank = this.getPlayerRank(player)
            return Result.success(Player(player.id, player.nickname, player.score, playerRank))
        } ?: Result.failure(Error("Player already registered"))
    }

    override suspend fun all(): List<Player> {
        TODO("Not yet implemented")
    }

    override suspend fun withId(id: PlayerId): Player? {
        TODO("Not yet implemented")
    }

    override suspend fun update(player: Player): Result<Player> {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }

    private fun getPlayerRank(player: Player): PlayerRank {
        TODO("Not yet implemented")
//        return PlayerRank(database.countDocuments(Filters.gte("score", player.score.value)).toInt() + 1)
    }
}