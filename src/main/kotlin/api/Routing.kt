package org.tournament.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.tournament.application.AddPlayer
import org.tournament.application.UpdatePlayerScore
import org.tournament.domain.*

@Serializable
data class CreatePlayerApi(val nickname: String)

@Serializable
data class UpdatePlayerScoreApi(val score: Int)

@Serializable
data class PlayerApi(val id: String, val nickname: String, val score: Int, val rank: Int)

fun Application.configureRouting() {

    val allPlayers by inject<AllPlayers>()

    val addPlayer by inject<AddPlayer>()

    val updatePlayerScore by inject<UpdatePlayerScore>()

    routing {
        get("/players") {
            call.respond(
                allPlayers.all().map { PlayerApi(it.id.value, it.nickname.value, it.score.value, it.rank?.value ?: 0) })
        }
        get("/players/{id}") {
            val playerId = call.parameters["id"]!!
            val player = allPlayers.withId(PlayerId(playerId))
            player?.let {
                call.respond(PlayerApi(player.id.value, player.nickname.value, player.score.value, it.rank?.value ?: 0))
            } ?: run {
                call.respondText("Player not found", status = HttpStatusCode.NotFound)
            }
        }
        post("/players") {
            val player = call.receive<CreatePlayerApi>()
            val playerAdded = addPlayer.run(Player.new(PlayerNickname(player.nickname)))
            if (playerAdded.isSuccess)
                call.respondText("Welcome to the tournament ${player.nickname}!!", status = HttpStatusCode.Created)
            else
                call.respondText("Player already registered ${player.nickname}!!", status = HttpStatusCode.Conflict)
        }
        put("/players/{id}/score") {
            val playerId = call.parameters["id"]!!
            val playerScore = call.receive<UpdatePlayerScoreApi>()
            val playerUpdated = updatePlayerScore.run(PlayerId(playerId), PlayerScore(playerScore.score))
            playerUpdated.onSuccess {
                call.respond(PlayerApi(it.id.value, it.nickname.value, it.score.value, it.rank?.value ?: 0))
            }.onFailure {
                call.respondText("Player not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
