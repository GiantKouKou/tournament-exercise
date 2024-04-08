package org.tournament.domain

import java.util.*

data class PlayerNickname(val value: String)

data class PlayerId(val value: String) {
    companion object Factory {
        fun random() = PlayerId(UUID.randomUUID().toString())
    }
}

class Player(
    val id: PlayerId,
    val nickname: PlayerNickname
)