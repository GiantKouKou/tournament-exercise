package org.tournament.domain

interface AllPlayers {
    fun add(player: Player): Result<Player>
    fun all(): List<Player>
    fun withId(id: PlayerId): Player?
    fun update(player: Player): Result<Player>
    fun clear()
}