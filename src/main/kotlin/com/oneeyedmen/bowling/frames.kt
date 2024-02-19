package com.oneeyedmen.bowling

sealed interface Frame {
    val roll1: PinCount? get() = null
    val roll2: PinCount? get() = null

    fun bonusScoreFor(
        nextFrame: Frame?,
        nextNextFrame: Frame?
    ) = Score(0)
}

sealed interface PlayableFrame : Frame {
    fun roll(pinCount: PinCount): Frame
}

sealed interface CompletedFrame : Frame

class UnplayedFrame : PlayableFrame {
    override fun roll(pinCount: PinCount) = when {
        pinCount.value == 10 -> Strike()
        else -> InProgressFrame(pinCount)
    }

    val totalPinCount = PinCount(0)
}

class UnplayedFinalFrame : PlayableFrame {
    override fun roll(pinCount: PinCount) =
        InProgressFinalFrame(pinCount)

    val totalPinCount = PinCount(0)
}

class InProgressFrame(
    override val roll1: PinCount
) : PlayableFrame {
    override fun roll(pinCount: PinCount) =
        NormalCompletedFrame(roll1, pinCount)

    val totalPinCount = roll1
}

class InProgressFinalFrame(
    override val roll1: PinCount
) : PlayableFrame {
    override fun roll(pinCount: PinCount) = when {
        (roll1 + pinCount).value >= 10 -> BonusInProgressFinalFrame(roll1, pinCount)
        else -> NormalCompletedFinalFrame(roll1, pinCount)
    }

    val totalPinCount = roll1
}

class BonusInProgressFinalFrame(
    override val roll1: PinCount,
    override val roll2: PinCount
) : PlayableFrame {
    override fun roll(pinCount: PinCount) =
        BonusCompletedFinalFrame(roll1, roll2, pinCount)

    val totalPinCount: Score = roll1 + roll2
    val isSpare: Boolean get() = totalPinCount.value == 10
}

class Strike : CompletedFrame {
    override val roll1: PinCount = PinCount(10)
    val totalPinCount = roll1

    override fun bonusScoreFor(
        nextFrame: Frame?,
        nextNextFrame: Frame?
    ): Score {
        val nextRoll = nextFrame?.roll1
        val nextNextRoll = nextFrame?.roll2 ?: nextNextFrame?.roll1
        return when {
            nextRoll != null && nextNextRoll != null -> nextRoll + nextNextRoll
            nextRoll != null && nextNextRoll == null -> Score(nextRoll.value)
            else -> Score(0)
        }
    }
}

class NormalCompletedFrame(
    override val roll1: PinCount,
    override val roll2: PinCount
) : CompletedFrame {
    val totalPinCount = roll1 plusAsPinCount roll2
    val isSpare: Boolean get() = totalPinCount.value == 10

    override fun bonusScoreFor(
        nextFrame: Frame?,
        nextNextFrame: Frame?
    ) = when {
        isSpare -> Score(nextFrame?.roll1?.value ?: 0)
        else -> Score(0)
    }
}

class NormalCompletedFinalFrame(
    override val roll1: PinCount,
    override val roll2: PinCount
) : CompletedFrame {
    val totalPinCount = roll1 plusAsPinCount roll2
    val isSpare: Boolean get() = totalPinCount.value == 10
}

class BonusCompletedFinalFrame(
    override val roll1: PinCount,
    override val roll2: PinCount,
    val roll3: PinCount
) : CompletedFrame {
    val totalPinCount = roll1 + roll2 + roll3
    val isSpare: Boolean get() = (roll1 + roll2).value == 10
}