package org.tournament.domain

interface AllPlayers {
    suspend fun add(player: Player): Result<Player>
    suspend fun all(): List<Player>
    suspend fun withId(id: PlayerId): Player?
    suspend fun update(player: Player): Result<Player>
    suspend fun clear()
}