package org.tournament.domain

interface AllPlayers {
    fun add(player: Player): Result<Unit>
    fun all(): List<Player>
}