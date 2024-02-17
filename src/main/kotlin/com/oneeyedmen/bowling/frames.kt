package com.oneeyedmen.bowling

sealed interface Frame {
    val roll1: PinCount? get() = null
    val roll2: PinCount? get() = null
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
    val isSpare: Boolean get() = roll1.value + roll2.value == 10
}

class Strike : CompletedFrame {
    override val roll1: PinCount = PinCount(10)
    val totalPinCount = roll1
}

class NormalCompletedFrame(
    override val roll1: PinCount,
    override val roll2: PinCount
) : CompletedFrame {
    val isSpare: Boolean get() = roll1.value + roll2.value == 10
    val totalPinCount = PinCount(roll1.value + roll2.value)
}

class NormalCompletedFinalFrame(
    override val roll1: PinCount,
    override val roll2: PinCount
) : CompletedFrame {
    val isSpare: Boolean get() = roll1.value + roll2.value == 10
    val totalPinCount = PinCount(roll1.value + roll2.value)
}

class BonusCompletedFinalFrame(
    override val roll1: PinCount,
    override val roll2: PinCount,
    val roll3: PinCount
) : CompletedFrame {
    val isSpare: Boolean get() = roll1.value + roll2.value == 10
    val totalPinCount = Score(roll1.value + roll2.value + roll3.value)
}