package com.oneeyedmen.bowling

fun List<Frame>.toScores(): List<Score> =
    windowed(size = 3, step = 1, partialWindows = true)
        .runningFold(Score(0)) { score, window ->
            val thisFrame = window.first()
            score + thisFrame.score(
                nextFrame = window.getOrNull(1),
                nextNextFrame = window.getOrNull(2)
            )
        }.drop(1)

private fun Frame.score(nextFrame: Frame?, nextNextFrame: Frame?): Score =
    bonusScoreFor(
        nextFrame = nextFrame,
        nextNextFrame = nextNextFrame
    ) + totalPinCount

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
