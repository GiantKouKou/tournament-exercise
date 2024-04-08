package org.tournament.domain

data class PlayerNickname(val value: String)

class Player(val nickname: PlayerNickname)