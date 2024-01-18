package com.oneeyedmen.bowling

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import kotlin.test.assertTrue

class BowlingTests {

    @Test
    fun `a game with no players isnt playable`() {
        val game = Game()
        expectThat(game).isA<CompletedGame>()
    }

    @Test
    fun `a game with players is playable`() {
        expectThat(Game("Fred")).isA<PlayableGame>()
        expectThat(Game("Fred", "Barney")).isA<PlayableGame>()
    }

    @Disabled("WIP")
    @Test
    fun `play a game`() {
        val game: Game = Game("Fred", "Barney")
        assertTrue(game is PlayableGame)
        expectThat(game.currentPlayer).isEqualTo("Fred")
//        val game2: Game = game.roll(PinCount(1))
//        assertTrue(game2 is PlayableGame)
//        val game3: Game = game.roll(PinCount(1))
    }

    @Test
    fun `play a game with just one player and one frame`() {
        val game: Game = Game("Fred", frameCount = 1)
        assertTrue(game is PlayableGame)
        expectThat(game.currentPlayer).isEqualTo("Fred")
        val game2: Game = game.roll(PinCount(1))
        assertTrue(game2 is PlayableGame)
        expectThat(game.currentPlayer).isEqualTo("Fred")
        val game3: Game = game2.roll(PinCount(1))
        assertTrue(game3 is CompletedGame)
    }

    @Test
    fun `line`() {
        val line = PlayableLine(
            PlayerName("Fred"),
            listOf(UnplayedFrame())
        )
        val line2 = line.roll(PinCount(1))
        assertTrue(line2 is PlayableLine)
        val line3 = line2.roll(PinCount(1))
        assertTrue(line3 is CompletedLine)
    }
}