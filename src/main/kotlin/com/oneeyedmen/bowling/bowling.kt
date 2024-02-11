package com.oneeyedmen.bowling

@JvmInline
value class Score(val value: Int) {
    init {
        require(value in 0..300)
    }

    override fun toString() = value.toString()

    operator fun plus(other: Score) = Score(value + other.value)
    operator fun plus(other: Int) = Score(value + other)
    operator fun plus(other: PinCount) = Score(value + other.value)
}

@JvmInline
value class PinCount(val value: Int) {
    init {
        require(value in 0..10)
    }

    override fun toString(): String = when (value) {
        0 -> "-"
        else -> value.toString()
    }

    operator fun plus(other: PinCount) = Score(value + other.value)
}

internal fun <E> List<E>.replacing(item: E, newItem: E): List<E> =
    map {
        if (it == item) newItem else it
    }

@Suppress("unused")
object Unuseds {
    @JvmInline
    value class PlayerName(val value: String)
    class Player
    class Foul
}


