package com.oneeyedmen.bowling

fun Game.toScorecard(): List<String> =
    lines.map { line -> line.toScorecard() }

private fun Line.toScorecard(): String =
    (listOf(playerName) + frames.toScorecard())
        .joinToString(" ")

private fun List<Frame>.toScorecard(): List<String> =
    zip(toScores()) { frame, score ->
        val scoreString = when (frame) {
            is UnplayedFrame, is UnplayedFinalFrame -> "   "
            else -> score.toString().padStart(3, '0')
        }
        frame.toScorecard() + " " + scoreString
    }

private fun Frame.toScorecard() = when (this) {
    is UnplayedFrame -> "[ ][ ]"
    is UnplayedFinalFrame -> "[ ][ ][ ]"
    is InProgressFrame -> "[${roll1}][ ]"
    is InProgressFinalFrame -> "[${roll1}][ ][ ]"
    is Strike -> "[ ][X]"
    is NormalCompletedFrame -> "[$roll1][${roll2.slashIf(isSpare)}]"
    is NormalCompletedFinalFrame -> "[$roll1][${roll2.slashIf(isSpare)}][ ]"
    is BonusInProgressFinalFrame -> "[$roll1][${roll2.slashIf(isSpare)}][ ]"
    is BonusCompletedFinalFrame -> "[$roll1][${roll2.slashIf(isSpare)}][$roll3]"
}

private fun PinCount.slashIf(isSpare: Boolean) = when {
    isSpare -> "/"
    else -> this.toString()
}