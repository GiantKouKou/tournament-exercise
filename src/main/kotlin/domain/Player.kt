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

data class PlayerRank(val value: Int) {
    init {
        require(value > 0)
    }
}

class Player(
    val id: PlayerId,
    val nickname: PlayerNickname,
    var score: PlayerScore,
    val rank: PlayerRank?
) {
    companion object Factory {
        fun new(nickname: PlayerNickname) = Player(PlayerId.random(), nickname, PlayerScore(0), null)
    }
}