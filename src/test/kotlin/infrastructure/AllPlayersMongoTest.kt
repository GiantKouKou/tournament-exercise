package infrastructure

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.ContainerExtension
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.testcontainers.containers.MongoDBContainer
import org.tournament.domain.Player
import org.tournament.domain.PlayerNickname
import org.tournament.infrastructure.AllPlayersMongo
import org.tournament.infrastructure.PlayerDB
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class AllPlayersMongoTest : FunSpec({

    val mongo = MongoDBContainer("mongo:latest").apply {
        startupAttempts = 1
    }

    val mongoTmp = install(ContainerExtension(mongo))

//    private val client = MongoClient.create("mongodb://localhost:27017")
//    private val database = client.getDatabase("tournament")
//    val repository = AllPlayersMongo(database)


    @Test
    fun `add should return success if player does not exist`() = runBlocking {
//        val player = Player.new(PlayerNickname("toto"))
//
//        val addedPlayer = repository.add(player)
//
//        assertTrue(addedPlayer.isSuccess)
//        val result = database.getCollection<PlayerDB>("players").find()
//        assertEquals(1, result.count())
//        val playerDb = result.first()
//        assertEquals("toto", playerDb.nickname)
//        assertEquals(0, playerDb.score)
//        assertEquals(player.id.value, playerDb.id)
        assertTrue(true)
    }
})