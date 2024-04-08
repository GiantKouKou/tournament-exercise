package org.tournament.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.tournament.application.AddPlayer
import org.tournament.domain.AllPlayers
import org.tournament.domain.Player
import org.tournament.domain.PlayerNickname

@Serializable
data class PlayerApi(val nickname: String)

fun Application.configureRouting() {

    val allPlayers by inject<AllPlayers>()

    val addPlayer by inject<AddPlayer>()

    routing {
        get("/players") {
            call.respond(allPlayers.all().map { PlayerApi(it.nickname.value) })
        }
        post("/players") {
            val player = call.receive<PlayerApi>()
            val playerAdded = addPlayer.run(Player(PlayerNickname(player.nickname)))
            if (playerAdded.isSuccess)
                call.respondText("Welcome to the tournament ${player.nickname}!!", status = HttpStatusCode.Created)
            else
                call.respondText("Player already registered ${player.nickname}!!", status = HttpStatusCode.Conflict)
        }
    }
}
