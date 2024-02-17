package com.oneeyedmen.bowling

fun List<Frame>.toScores(): List<Score> =
    windowed(size = 3, step = 1, partialWindows = true)
        .runningFold(Score(0)) { score, window ->
            val thisFrame = window.first()
            val bonusScore = thisFrame.bonusScoreFor(
                nextFrame = window.getOrNull(1),
                nextNextFrame = window.getOrNull(2)
            )
            score + bonusScore + thisFrame.totalPinCount
        }.drop(1)

private fun Frame.bonusScoreFor(
    nextFrame: Frame?,
    nextNextFrame: Frame?
) = when {
    this is Strike -> {
        val nextRoll = nextFrame?.roll1
        val nextNextRoll = nextFrame?.roll2 ?: nextNextFrame?.roll1
        when {
            nextRoll != null && nextNextRoll != null -> nextRoll + nextNextRoll
            nextRoll != null && nextNextRoll == null -> Score(nextRoll.value)
            else -> Score(0)
        }
    }

    this is NormalCompletedFrame && isSpare -> {
        val nextRoll = nextFrame?.roll1
        Score(nextRoll?.value ?: 0)
    }

    else -> Score(0)
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
