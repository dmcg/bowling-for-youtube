package com.oneeyedmen.bowling

fun Game.toScorecard(): String =
    lines.joinToString("\n") { line -> line.toScorecard() }

private fun Line.toScorecard(): String =
    (listOf(playerName) + frames.map { it.toScorecard() })
        .joinToString(" ")

private fun Frame.toScorecard() = when(this) {
    is UnplayedFrame -> "[ ][ ] 000"
    is InProgressFrame -> "[${roll1}][ ] 000"
    is NormalCompletedFrame -> toScorecard()
    is Strike -> "[ ][X] 000"
}

@Suppress("IMPLICIT_CAST_TO_ANY")
private fun NormalCompletedFrame.toScorecard() =
    "[${roll1}][${if (isSpare) "/" else roll2}] 000"