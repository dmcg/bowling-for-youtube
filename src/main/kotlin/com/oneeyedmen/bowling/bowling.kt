package com.oneeyedmen.bowling

class Game {
    val lines: List<Line> = TODO()
}

class Line {
    val playerName: PlayerName = TODO()
    val frames: List<Frame> = TODO()
}

@JvmInline
value class PlayerName(val value: String)

class Frame {
    val roll1: PinCount? = TODO()
    val roll2: PinCount? = TODO()
    val score: Score? = TODO()
}

@JvmInline
value class Score(val value: Int) {
    init { require(value in 0..300) }
}

@JvmInline
value class PinCount(val value: Int) {
    init { require(value in 0..10) }
}

class Player
class Foul


