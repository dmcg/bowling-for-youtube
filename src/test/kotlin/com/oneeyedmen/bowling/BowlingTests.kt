package com.oneeyedmen.bowling

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

    @Test
    fun `play a game`() {
        var game: Game
        game = Game("Fred", "Barney", frameCount = 2) as PlayableGame
        expectThat(game.currentPlayer).isEqualTo("Fred")
        game = game.roll(PinCount(1)) as PlayableGame
        expectThat(game.currentPlayer).isEqualTo("Fred")
        game = game.roll(PinCount(2)) as PlayableGame
        expectThat(game.currentPlayer).isEqualTo("Barney")
        game = game.roll(PinCount(3)) as PlayableGame
        expectThat(game.currentPlayer).isEqualTo("Barney")
        game = game.roll(PinCount(4)) as PlayableGame
        expectThat(game.currentPlayer).isEqualTo("Fred")
        game = game.roll(PinCount(5)) as PlayableGame
        game = game.roll(PinCount(6)) as PlayableGame
        expectThat(game.currentPlayer).isEqualTo("Barney")
        game = game.roll(PinCount(7)) as PlayableGame
        game = game.roll(PinCount(8))
        assertTrue(game is CompletedGame)
    }

    @Test
    fun `play a game with just one player and one frame`() {
        var game: Game
        game = Game("Fred", frameCount = 1) as PlayableGame
        expectThat(game.currentPlayer).isEqualTo("Fred")
        game = game.roll(PinCount(1)) as PlayableGame
        expectThat(game.currentPlayer).isEqualTo("Fred")
        game = game.roll(PinCount(1))
        assertTrue(game is CompletedGame)
    }

    @Test
    fun `line`() {
        var line: Line
        line = PlayableLine(
            PlayerName("Fred"),
            listOf(UnplayedFrame())
        )
        line = line.roll(PinCount(1)) as PlayableLine
        line = line.roll(PinCount(1))
        assertTrue(line is CompletedLine)
    }
}