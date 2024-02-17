package com.oneeyedmen.bowling

fun List<Frame>.toScores(): List<Score> {
    var score = Score(0)
    val scores: List<Score> = mapIndexed { index, frame ->
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
        score
    }
    return scores
}

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
