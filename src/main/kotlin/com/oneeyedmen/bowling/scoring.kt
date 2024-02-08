package com.oneeyedmen.bowling

fun Game.toScorecard(): List<String> =
    lines.map { line -> line.toScorecard() }

private fun Line.toScorecard(): String =
    (listOf(playerName) + frames.toScorecard())
        .joinToString(" ")

private fun List<Frame>.toScorecard(): List<String> {
    var score = Score(0)
    return mapIndexed { index, frame ->
        score += frame.totalPinCount
        if (frame is Strike) {
            score += this[index + 1].totalPinCount
        } else if (frame is NormalCompletedFrame && frame.isSpare) {
            val nextRoll = this.getOrNull(index + 1)?.roll1
            if (nextRoll != null)
                score += nextRoll
        }
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

private val Frame.roll1: PinCount?
    get() = when (this) {
        is NormalCompletedFrame -> this.roll1
        is Strike -> this.roll1
        is InProgressFrame -> this.roll1
        is UnplayedFrame -> null
    }
