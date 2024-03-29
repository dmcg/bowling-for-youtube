package com.oneeyedmen.bowling

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import kotlin.test.assertTrue

class BowlingTests {

    @Test
    fun `a game with no players isnt playable`() {
        expectThat(Game()).isA<CompletedGame>()
    }

    @Test
    fun `a game with no frames isnt playable`() {
        expectThat(Game("Fred", "Barney", frameCount = 0)).isA<CompletedGame>()
    }

    @Test
    fun `can't create an invalid PlayableGame`() {
        expectThrows<IllegalArgumentException> {
            PlayableGame(emptyList(), frameCount = 1)
        }
        expectThrows<IllegalArgumentException> {
            PlayableGame(listOf("Fred"), frameCount = 0)
        }
        expectThrows<IllegalArgumentException> {
            PlayableGame(listOf("Fred"), frameCount = -1)
        }
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
        game = game.roll(PinCount(4)) as PlayableGame
        expectThat(game.currentPlayer).isEqualTo("Barney")
        game = game.roll(PinCount(3)) as PlayableGame
        game = game.roll(PinCount(2))
        assertTrue(game is CompletedGame)
    }

    @Test
    fun `can't create invalid PinCounts`() {
        expectThrows<IllegalArgumentException> {
            PinCount(11)
        }
        expectThrows<IllegalArgumentException> {
            PinCount(-1)
        }
    }

    @Test
    fun `can't roll invalid PinCounts in a non-final frame`() {
        var game: Game
        game = Game("Fred", frameCount = 2) as PlayableGame
        game = game.roll(PinCount(9)) as PlayableGame
        expectThrows<IllegalArgumentException> {
            game.roll(PinCount(2))
        }
    }

    @Test
    fun `can't roll invalid PinCounts in a final frame`() {
        var game: Game
        game = Game("Fred", frameCount = 1) as PlayableGame
        game = game.roll(PinCount(9)) as PlayableGame
        expectThrows<IllegalArgumentException> {
            game.roll(PinCount(2))
        }
    }

    @Test
    fun `can't roll invalid PinCounts in a strike final frame`() {
        var game: Game
        game = Game("Fred", frameCount = 1) as PlayableGame
        game = game.roll(PinCount(10)) as PlayableGame
        game = game.roll(PinCount(9)) as PlayableGame
        expectThrows<IllegalArgumentException> {
            game.roll(PinCount(2))
        }
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
    fun `render pathological scorecards`() {
        expectThat(Game().toScorecard()).isEqualTo(emptyList())

        Game("Fred  ", "Barney", frameCount = 0).expectScoreboard(
            "Fred  ",
            "Barney"
        )
    }

    @Test
    fun `render scorecard`() {
        var game: Game
        game = Game("Fred  ", "Barney", frameCount = 2) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][ ]     [ ][ ][ ]    ",
            "Barney [ ][ ]     [ ][ ][ ]    "
        )
        game = game.roll(PinCount(1)) as PlayableGame
        game.expectScoreboard(
            "Fred   [1][ ] 001 [ ][ ][ ]    ",
            "Barney [ ][ ]     [ ][ ][ ]    "
        )
        game = game.roll(PinCount(2)) as PlayableGame
        game.expectScoreboard(
            "Fred   [1][2] 003 [ ][ ][ ]    ",
            "Barney [ ][ ]     [ ][ ][ ]    "
        )
        game = game.roll(PinCount(10)) as PlayableGame
        game.expectScoreboard(
            "Fred   [1][2] 003 [ ][ ][ ]    ",
            "Barney [ ][X] 010 [ ][ ][ ]    "
        )
        game = game.roll(PinCount(9)) as PlayableGame
        game.expectScoreboard(
            "Fred   [1][2] 003 [9][ ][ ] 012",
            "Barney [ ][X] 010 [ ][ ][ ]    "
        )
        game = game.roll(PinCount(0)) as PlayableGame
        game.expectScoreboard(
            "Fred   [1][2] 003 [9][-][ ] 012",
            "Barney [ ][X] 010 [ ][ ][ ]    "
        )
        game = game.roll(PinCount(0)) as PlayableGame
        game.expectScoreboard(
            "Fred   [1][2] 003 [9][-][ ] 012",
            "Barney [ ][X] 010 [-][ ][ ] 010"
        )
        game = game.roll(PinCount(0)) as CompletedGame
        game.expectScoreboard(
            "Fred   [1][2] 003 [9][-][ ] 012",
            "Barney [ ][X] 010 [-][-][ ] 010"
        )
    }

    @Test
    fun `render strikes`() {
        var game: Game
        game = Game("Fred  ", frameCount = 2) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][ ]     [ ][ ][ ]    "
        )
        game = game.roll(PinCount(10)) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][X] 010 [ ][ ][ ]    "
        )
        game = game.roll(PinCount(1)) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][X] 011 [1][ ][ ] 012"
        )
        game = game.roll(PinCount(2)) as CompletedGame
        game.expectScoreboard(
            "Fred   [ ][X] 013 [1][2][ ] 016"
        )
    }

    @Test
    fun `render spares`() {
        var game: Game
        game = Game("Fred  ", frameCount = 2) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][ ]     [ ][ ][ ]    "
        )
        game = game.roll(PinCount(9)) as PlayableGame
        game.expectScoreboard(
            "Fred   [9][ ] 009 [ ][ ][ ]    "
        )
        game = game.roll(PinCount(1)) as PlayableGame
        game.expectScoreboard(
            "Fred   [9][/] 010 [ ][ ][ ]    "
        )
        game = game.roll(PinCount(2)) as PlayableGame
        game.expectScoreboard(
            "Fred   [9][/] 012 [2][ ][ ] 014"
        )
        game = game.roll(PinCount(3)) as CompletedGame
        game.expectScoreboard(
            "Fred   [9][/] 012 [2][3][ ] 017"
        )
    }

    @Test
    fun `final frame spares`() {
        var game: Game
        game = Game("Fred  ", frameCount = 2) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][ ]     [ ][ ][ ]    "
        )
        game = game.roll(PinCount(9)) as PlayableGame
        game.expectScoreboard(
            "Fred   [9][ ] 009 [ ][ ][ ]    "
        )
        game = game.roll(PinCount(1)) as PlayableGame
        game.expectScoreboard(
            "Fred   [9][/] 010 [ ][ ][ ]    "
        )
        game = game.roll(PinCount(2)) as PlayableGame
        game.expectScoreboard(
            "Fred   [9][/] 012 [2][ ][ ] 014"
        )
        game = game.roll(PinCount(8)) as PlayableGame
        game.expectScoreboard(
            "Fred   [9][/] 012 [2][/][ ] 022"
        )
        game = game.roll(PinCount(5)) as CompletedGame
        game.expectScoreboard(
            "Fred   [9][/] 012 [2][/][5] 027"
        )
    }

    @Test
    fun `run of strikes`() {
        var game: Game
        game = Game("Fred  ", frameCount = 4) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][ ]     [ ][ ]     [ ][ ]     [ ][ ][ ]    "
        )
        game = game.roll(PinCount(10)) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][X] 010 [ ][ ]     [ ][ ]     [ ][ ][ ]    "
        )
        game = game.roll(PinCount(10)) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][X] 020 [ ][X] 030 [ ][ ]     [ ][ ][ ]    "
        )
        game = game.roll(PinCount(10)) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][X] 030 [ ][X] 050 [ ][X] 060 [ ][ ][ ]    "
        )
        game = game.roll(PinCount(10)) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][X] 030 [ ][X] 060 [ ][X] 080 [X][ ][ ] 090"
        )
        game = game.roll(PinCount(10)) as PlayableGame
        game.expectScoreboard(
            "Fred   [ ][X] 030 [ ][X] 060 [ ][X] 090 [X][X][ ] 110"
        )
        game = game.roll(PinCount(10)) as CompletedGame
        game.expectScoreboard(
            "Fred   [ ][X] 030 [ ][X] 060 [ ][X] 090 [X][X][X] 120"
        )
    }

    @Test
    fun `maximum score is 300`() {
        val rolls = List(12) { PinCount(10) }
        val games: List<Game> = rolls.runningFold(Game("Fred")) { game, pinCount ->
            (game as PlayableGame).roll(pinCount)
        }
//        games.forEach { println(it.toScorecard()) }
        expectThat(
            games.last().lines.first().frames.toScores().last()
        ).isEqualTo(Score(300))
    }
}

private fun Game.expectScoreboard(vararg lines: String) {
    expectThat(toScorecard()).isEqualTo(
        lines.toList()
    )
}

