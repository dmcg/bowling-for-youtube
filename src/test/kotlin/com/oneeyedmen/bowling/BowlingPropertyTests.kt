package com.oneeyedmen.bowling

import net.jqwik.api.Arbitrary
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.api.constraints.NotEmpty
import net.jqwik.api.constraints.Size
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.combine
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class BowlingPropertyTests {

    @Property
    fun `a game with no players isnt playable`(
        @ForAll("nonNegativeIntegers") aNumberOfFrames: Int
    ) {
        expectThat(Game(emptyList(), frameCount = aNumberOfFrames)).isA<CompletedGame>()
    }

    @Property
    fun `a game with no frames isnt playable`(
        @ForAll aListOfPlayerNames: List<String>
    ) {
        expectThat(Game(aListOfPlayerNames, frameCount = 0)).isA<CompletedGame>()
    }

    @Property
    fun `cannot create a game with negative frame count`(
        @ForAll aListOfPlayerNames: List<String>,
        @ForAll("negativeIntegers") aNumberOfFrames: Int
    ) {
        expectThrows<IllegalArgumentException> {
            Game(aListOfPlayerNames, frameCount = aNumberOfFrames)
        }
    }

    @Property
    fun `a game with players is playable`(
        @ForAll @NotEmpty aListOfPlayerNames: List<String>,
        @ForAll("validFrameCount") aNumberOfFrames: Int
    ) {
        expectThat(Game(aListOfPlayerNames, aNumberOfFrames)).isA<PlayableGame>()
    }

    @Property
    fun `a completed line with no strikes or spares scores the total of the pincounts`(
        @ForAll("smallPinCounts") @Size(20) pinCounts: List<PinCount>
    ) {
        val endGame = Game("Fred").rollAll(pinCounts)
        val expectedScore = Score(pinCounts.sumOf { it.value })
        expectThat(endGame) {
            isA<CompletedGame>()
            get { scores() }.isEqualTo(listOf(expectedScore))
        }
    }

    @Property
    fun `there are no errors in a valid game`(
        @ForAll("validTurns") @Size(9) turns: List<ValidTurn>,
        @ForAll("validFinalTurn") finalTurn: ValidFinalTurn
    ) {
        val endGame = Game("Fred").rollAll(turns + finalTurn)
        expectThat(endGame).isA<CompletedGame>()
    }

    @Property
    fun `there are no errors in a valid two player game`(
        @ForAll("validTurns") @Size(9) turns1: List<ValidTurn>,
        @ForAll("validFinalTurn") finalTurn1: ValidFinalTurn,
        @ForAll("validTurns") @Size(9) turns2: List<ValidTurn>,
        @ForAll("validFinalTurn") finalTurn2: ValidFinalTurn,
    ) {
        val interleavedTurns = (turns1 + finalTurn1).zip(turns2 + finalTurn2).flatMap { listOf(it.first, it.second) }
        val endGame = Game("Fred", "Barney").rollAll(interleavedTurns)
        expectThat(endGame) {
            isA<CompletedGame>()
        }
    }

    @Provide
    fun nonNegativeIntegers(): Arbitrary<Int> = Int.any(0..Int.MAX_VALUE)

    @Provide
    fun negativeIntegers(): Arbitrary<Int> = Int.any(Int.MIN_VALUE..-1)

    @Provide
    fun validFrameCount(): Arbitrary<Int> = Int.any(1..10)

    @Provide
    fun smallPinCounts(): Arbitrary<List<PinCount>> = smallPinCount().list()

    private fun smallPinCount(): Arbitrary<PinCount> = Int.any(0..4).map { PinCount(it) }

    @Provide
    fun validPinCounts(): Arbitrary<List<PinCount>> = validPinCount().list().ofSize(20)

    private fun validPinCount(): Arbitrary<PinCount> = Int.any(0..10).map { PinCount(it) }

    @Provide
    fun validTurns(): Arbitrary<List<ValidTurn>> = validTurn().list()

    private fun validTurn(): Arbitrary<ValidTurn> = combine(validPinCount(), validPinCount()) { r1, r2 ->
        when {
            r1.value == 10 -> ValidTurn(r1, null)
            (r1 + r2).value > 10 -> ValidTurn(r1, PinCount(10 - r1.value))
            else -> ValidTurn(r1, r2)
        }
    }

    @Provide
    fun validFinalTurn(): Arbitrary<ValidFinalTurn> =
        combine(validPinCount(), validPinCount(), validPinCount()) { r1, r2, r3 ->
            when {
                r1.value == 10 && r2.value == 10 -> ValidFinalTurn(r1, r2, r3)
                r1.value == 10 && (r2.value + r3.value) >= 10 -> ValidFinalTurn(r1, r2, PinCount(10 - r2.value))
                r1.value + r2.value >= 10 -> ValidFinalTurn(r1, PinCount(10 - r1.value), r3)
                else -> ValidFinalTurn(r1, r2, null)
            }
        }
}

interface Turn {
    val rolls: List<PinCount>
}

data class ValidTurn(val roll1: PinCount, val roll2: PinCount?) : Turn {
    init {
        roll2?.plusAsPinCount(roll1)
    }

    override val rolls = listOfNotNull(roll1, roll2)
}

data class ValidFinalTurn(val roll1: PinCount, val roll2: PinCount?, val roll3: PinCount?) : Turn {
    override val rolls = listOfNotNull(roll1, roll2, roll3)
}

private fun Game.scores() = lines.map { it.frames.toScores().last() }

private fun Game.rollAll(
    pinCounts: List<PinCount>
) = pinCounts.fold(this) { game, pinCount ->
    (game as PlayableGame).roll(pinCount)
}

@JvmName("rollAllValidTurns")
private fun Game.rollAll(turns: List<Turn>): Game {
    val pinCounts: List<PinCount> = turns.flatMap { it.rolls }
    return this.rollAll(pinCounts)
}
