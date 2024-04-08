package org.tournament.application

import org.tournament.domain.AllPlayers
import org.tournament.domain.Player

class AddPlayer(private val allPlayers: AllPlayers) {
    fun run(player: Player) = allPlayers.add(player)
}