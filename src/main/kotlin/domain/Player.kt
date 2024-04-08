package org.tournament.domain

import java.util.*

data class PlayerNickname(val value: String)

data class PlayerId(val value: UUID) {
    companion object Factory {
        fun random() = PlayerId(UUID.randomUUID())
    }
}

class Player(
    val id: PlayerId,
    val nickname: PlayerNickname
)