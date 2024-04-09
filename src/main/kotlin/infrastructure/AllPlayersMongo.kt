package org.tournament.infrastructure

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.codecs.pojo.annotations.BsonId
import org.tournament.domain.*

data class PlayerDB(@BsonId val id: String, val nickname: String, val score: Int)

class AllPlayersMongo(mongoDatabase: MongoDatabase) : AllPlayers {

    companion object {
        const val PLAYERS_COLLECTION = "players"
    }

    private val database = mongoDatabase.getCollection<PlayerDB>(PLAYERS_COLLECTION)

    override suspend fun add(player: Player): Result<Player> {
        return try {
            database.insertOne(
                PlayerDB(player.id.value, player.nickname.value, player.score.value)
            )
            val playerRank = this.getPlayerRank(player.score)
            Result.success(Player(player.id, player.nickname, player.score, playerRank))
        } catch (e: Error) {
            print(e)
            Result.failure(e)
        }
    }

    override suspend fun all(): List<Player> =
        database.find().sort(Sorts.descending("score")).toList().mapIndexed { index, player ->
            Player(
                PlayerId(player.id),
                PlayerNickname(player.nickname),
                PlayerScore(player.score),
                PlayerRank(index + 1)
            )
        }

    override suspend fun withId(id: PlayerId): Player? {
        val playerDb = database.find(Filters.eq("_id", id.value)).firstOrNull()
        return playerDb?.let {
            val playerRank = this.getPlayerRank(PlayerScore(playerDb.score))
            Player(
                PlayerId(playerDb.id),
                PlayerNickname(playerDb.nickname),
                PlayerScore(playerDb.score),
                playerRank
            )
        }
    }

    override suspend fun update(player: Player): Result<Player> {
        val updateOne = database.updateOne(
            Filters.eq("_id", player.id.value),
            Updates.set("score", player.score.value)
        )
        return if (updateOne.modifiedCount.toInt() == 1)
            Result.success(
                Player(
                    player.id,
                    player.nickname,
                    player.score,
                    this.getPlayerRank(player.score)
                )
            )
        else
            Result.failure(Error("Not found"))
    }

    override suspend fun clear() {
        database.deleteMany(Filters.empty())
    }

    private suspend fun getPlayerRank(playerScore: PlayerScore): PlayerRank {
        return PlayerRank(database.countDocuments(Filters.gte("score", playerScore.value)).toInt())
    }
}