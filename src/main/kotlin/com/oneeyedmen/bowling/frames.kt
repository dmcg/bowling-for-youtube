package com.oneeyedmen.bowling

sealed interface Frame

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

class InProgressFrame(val roll1: PinCount) : PlayableFrame {
    override fun roll(pinCount: PinCount) =
        NormalCompletedFrame(roll1, pinCount)
    val totalPinCount = roll1
}

class Strike : CompletedFrame {
    val roll1: PinCount = PinCount(10)
    val totalPinCount = roll1
}

class NormalCompletedFrame(
    val roll1: PinCount,
    val roll2: PinCount
) : CompletedFrame {
    val isSpare: Boolean get() = roll1.value + roll2.value == 10
    val totalPinCount = PinCount(roll1.value + roll2.value)
}