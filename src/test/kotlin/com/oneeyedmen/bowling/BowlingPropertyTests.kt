package com.oneeyedmen.bowling

import net.jqwik.api.Arbitrary
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.api.constraints.NotEmpty
import net.jqwik.kotlin.api.any
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class BowlingPropertyTests {

    @Property
    fun `a game with no players isnt playable`(
        @ForAll("nonNegativeIntegers") aNumberOfFrames: Int
    ) = Game(emptyList(), frameCount = aNumberOfFrames) is CompletedGame

    @Property
    fun `a game with no frames isnt playable`(
        @ForAll aListOfPlayerNames: List<String>
    ) = Game(aListOfPlayerNames, frameCount = 0) is CompletedGame

    @Property
    fun `cannot create a game with negative frame count`(
        @ForAll aListOfPlayerNames: List<String>,
        @ForAll("negativeIntegers") aNumberOfFrames: Int
    ) = failsWith<IllegalArgumentException> {
        Game(aListOfPlayerNames, frameCount = aNumberOfFrames)
    }

    @Property
    fun `a game with players is playable`(
        @ForAll @NotEmpty aListOfPlayerNames: List<String>,
        @ForAll("validFrameCount") aNumberOfFrames: Int
    ) = Game(aListOfPlayerNames, aNumberOfFrames) is PlayableGame

    @Property
    fun `a completed line with no strikes or spares scores the total of the pincounts`(
        @ForAll("smallPinCounts") pinCounts: List<PinCount>
    ) {
        val endGame = Game("Fred").rollAll(pinCounts)
        val expectedScore = Score(pinCounts.sumOf { it.value })
        expectThat(endGame) {
            isA<CompletedGame>()
            get {scores()}.isEqualTo(listOf(expectedScore))
        }
    }

    @Provide
    fun nonNegativeIntegers(): Arbitrary<Int> = Int.any(0..Int.MAX_VALUE)

    @Provide
    fun negativeIntegers(): Arbitrary<Int> = Int.any(Int.MIN_VALUE..-1)

    @Provide
    fun validFrameCount(): Arbitrary<Int> = Int.any(1..10)

    @Provide
    fun smallPinCounts(): Arbitrary<List<PinCount>> = Int.any(0..4).map { PinCount(it) }.list().ofSize(20)

}

inline fun <reified X : Throwable> failsWith(f: () -> Unit) = try {
    f()
    false
} catch (x: Throwable) {
    x is X
}

private fun Game.scores() = lines.map { it.frames.toScores().last() }

private fun Game.rollAll(
    pinCounts: List<PinCount>
) = pinCounts.fold(this) { game, pinCount ->
    (game as PlayableGame).roll(pinCount)
}
