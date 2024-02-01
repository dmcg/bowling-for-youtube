package com.oneeyedmen.bowling

fun Game.toScorecard(): String =
    lines.joinToString("\n") { line -> line.toScorecard() }

private fun Line.toScorecard(): String =
    (listOf(playerName) + frames.toScorecard())
        .joinToString(" ") + "."

private fun List<Frame>.toScorecard(): List<String> {
    var score = Score(0)
    return map { frame ->
        score = score + frame.totalPinCount
        val scoreString = if (frame is UnplayedFrame) "   "
            else score.toString().padStart(3, '0')
        frame.toScorecard() + " " + scoreString
    }
}

private fun Frame.toScorecard() = when(this) {
    is UnplayedFrame -> "[ ][ ]"
    is InProgressFrame -> "[${roll1}][ ]"
    is NormalCompletedFrame -> toScorecard()
    is Strike -> "[ ][X]"
}

@Suppress("IMPLICIT_CAST_TO_ANY")
private fun NormalCompletedFrame.toScorecard() =
    "[${roll1}][${if (isSpare) "/" else roll2}]"