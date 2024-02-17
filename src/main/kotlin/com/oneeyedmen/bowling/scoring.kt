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
        when {
            frame is Strike -> {
                val nextFrame = this[index + 1]
                val nextRoll = nextFrame.roll1
                if (nextRoll != null) {
                    score += nextRoll
                    val nextNextRoll = nextFrame.roll2
                        ?: getOrNull(index + 2)?.roll1
                    if (nextNextRoll != null)
                        score += nextNextRoll
                }
            }
            frame is NormalCompletedFrame && frame.isSpare -> {
                val nextRoll = this.getOrNull(index + 1)?.roll1
                if (nextRoll != null)
                    score += nextRoll
            }
        }
        val scoreString = when (frame) {
            is UnplayedFrame, is UnplayedFinalFrame -> "   "
            else -> score.toString().padStart(3, '0')
        }
        frame.toScorecard() + " " + scoreString
    }
}

private fun Frame.toScorecard() = when(this) {
    is UnplayedFrame -> "[ ][ ]"
    is UnplayedFinalFrame -> "[ ][ ][ ]"
    is InProgressFrame -> "[${roll1}][ ]"
    is InProgressFinalFrame -> "[${roll1}][ ][ ]"
    is NormalCompletedFrame -> toScorecard()
    is NormalCompletedFinalFrame -> toScorecard()
    is Strike -> "[ ][X]"
    is BonusInProgressFinalFrame -> toScorecard()
    is BonusCompletedFinalFrame -> toScorecard()
}


@Suppress("IMPLICIT_CAST_TO_ANY")
private fun NormalCompletedFrame.toScorecard() =
    "[${roll1}][${if (isSpare) "/" else roll2}]"

@Suppress("IMPLICIT_CAST_TO_ANY")
private fun NormalCompletedFinalFrame.toScorecard() =
    "[${roll1}][${if (isSpare) "/" else roll2}][ ]"

@Suppress("IMPLICIT_CAST_TO_ANY")
private fun BonusCompletedFinalFrame.toScorecard() =
    "[${roll1}][${if (isSpare) "/" else roll2}][$roll3]"

@Suppress("IMPLICIT_CAST_TO_ANY")
private fun BonusInProgressFinalFrame.toScorecard() =
    "[${roll1}][${if (isSpare) "/" else roll2}][ ]"

private val Frame.totalPinCount: Int
    get() = when (this) {
        is NormalCompletedFrame -> this.totalPinCount.value
        is NormalCompletedFinalFrame -> this.totalPinCount.value
        is Strike -> this.totalPinCount.value
        is InProgressFrame -> this.totalPinCount.value
        is InProgressFinalFrame -> this.totalPinCount.value
        is UnplayedFrame -> this.totalPinCount.value
        is UnplayedFinalFrame -> this.totalPinCount.value
        is BonusInProgressFinalFrame -> this.totalPinCount.value
        is BonusCompletedFinalFrame -> this.totalPinCount.value
    }
