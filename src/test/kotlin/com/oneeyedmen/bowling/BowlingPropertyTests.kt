package com.oneeyedmen.bowling

import net.jqwik.api.*
import net.jqwik.kotlin.api.any

class BowlingPropertyTests {

    @Property
    fun `a game with no players isnt playable`(
        @ForAll("nonNegativeIntegers") aNumberOfFrames: Int
    ): Boolean {
        return Game(
            emptyList(),
            frameCount = aNumberOfFrames
        ) is CompletedGame
    }

    @Property
    fun `a game with no frames isnt playable`(
        @ForAll aListOfPlayerNames: List<String>
    ): Boolean {
        return Game(
            aListOfPlayerNames,
            frameCount = 0
        ) is CompletedGame
    }

    @Property
    fun `cannot create a game with negative frame count`(
        @ForAll aListOfPlayerNames: List<String>,
        @ForAll("negativeIntegers") aNumberOfFrames: Int
    ): Boolean {
        return try {
            Game(
                aListOfPlayerNames,
                frameCount = aNumberOfFrames
            )
            false
        } catch (x: IllegalArgumentException) {
            true
        }
    }

    @Property
    fun `a game with players is playable`(
        @ForAll aListOfPlayerNames: List<String>,
        @ForAll("validFrameCount") aNumberOfFrames: Int
    ): Boolean {
        Assume.that(aListOfPlayerNames.isNotEmpty())
        return Game(
            aListOfPlayerNames,
            aNumberOfFrames
        ) is PlayableGame
    }

    @Provide
    fun nonNegativeIntegers(): Arbitrary<Int> = Int.any(0..Int.MAX_VALUE)

    @Provide
    fun negativeIntegers(): Arbitrary<Int> = Int.any(Int.MIN_VALUE..-1)

    @Provide
    fun validFrameCount(): Arbitrary<Int> = Int.any(1..10)
}