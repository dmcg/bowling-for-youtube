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
                score += this[index + 1].totalPinCount
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
    is BonusInProgressFinalFrame -> "[${roll1}][/][ ]"
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

private val Frame.roll1: PinCount?
    get() = when (this) {
        is NormalCompletedFrame -> this.roll1
        is NormalCompletedFinalFrame -> this.roll1
        is Strike -> this.roll1
        is InProgressFrame -> this.roll1
        is InProgressFinalFrame -> this.roll1
        is UnplayedFrame -> null
        is UnplayedFinalFrame -> null
        is BonusInProgressFinalFrame -> this.roll1
        is BonusCompletedFinalFrame -> this.roll1
    }
