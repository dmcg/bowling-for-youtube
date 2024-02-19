package com.oneeyedmen.bowling

sealed interface Line {
    val playerName: String
    val frames: List<Frame>
}

class PlayableLine(
    override val playerName: String,
    override val frames: List<Frame>
) : Line {

    constructor(playerName: String, frameCount: Int) :
        this(playerName,
            List(frameCount) { index ->
                if (index == frameCount -1 )
                    UnplayedFinalFrame()
                else UnplayedFrame()
            })
    init {
        require(frames.isNotEmpty())
        require(frames.any { it is PlayableFrame })
    }

    fun roll(pinCount: PinCount): Line {
        val currentFrame: PlayableFrame = frames.find { it is PlayableFrame } as PlayableFrame
        val newFrame: Frame = currentFrame.roll(pinCount)
        val newFrames: List<Frame> = frames.replacing(currentFrame, newFrame)
        val completedFrames = newFrames.filterIsInstance<CompletedFrame>()
        return if (completedFrames.size == frames.size)
            CompletedLine(playerName, completedFrames)
        else
            PlayableLine(playerName, newFrames)
    }
}

class CompletedLine(
    override val playerName: String,
    override val frames: List<CompletedFrame>
) : Line