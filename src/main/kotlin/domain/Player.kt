package org.tournament.domain

import java.util.*

data class PlayerNickname(val value: String)

data class PlayerId(val value: String) {
    companion object Factory {
        fun random() = PlayerId(UUID.randomUUID().toString())
    }
}

data class PlayerScore(val value: Int) {
    init {
        require(value >= 0)
    }
}

class Player(
    val id: PlayerId,
    val nickname: PlayerNickname,
    var score: PlayerScore
) {
    companion object Factory {
        fun new(nickname: PlayerNickname) = Player(PlayerId.random(), nickname, PlayerScore(0))
    }
}