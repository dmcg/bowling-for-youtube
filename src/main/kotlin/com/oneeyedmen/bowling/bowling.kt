package com.oneeyedmen.bowling

sealed interface Game {
    val lines: List<Line>
}

data class PlayableGame(override val lines: List<Line>) : Game {
    fun roll(pinCount: PinCount): Game {
        val currentLine: PlayableLine = TODO()
        val newLine: Line = currentLine.roll(pinCount)
        val newLines: List<Line> = lines.replace(currentLine, newLine)
        val completedLines: List<CompletedLine> = lines.filterIsInstance<CompletedLine>()
        return if (completedLines.size < lines.size)
            PlayableGame(newLines)
        else
            CompletedGame(completedLines)
    }
}
data class CompletedGame(override val lines: List<CompletedLine>) : Game

sealed interface Line {
    val playerName: PlayerName
    val frames: List<Frame>
}
class PlayableLine(override val playerName: PlayerName, override val frames: List<Frame>) : Line {
    fun roll(pinCount: PinCount): Line {
        val currentFrame: PlayableFrame = TODO()
        val newFrame: Frame = currentFrame.roll(pinCount)
        val newFrames: List<Frame> = frames.replace(currentFrame, newFrame)
        val completedFrames: List<CompletedFrame> = frames.filterIsInstance<CompletedFrame>()
        return if (completedFrames.size < frames.size)
            PlayableLine(playerName, newFrames)
        else
            CompletedLine(playerName, completedFrames)
    }
}

class CompletedLine(
    override val playerName: PlayerName,
    override val frames: List<CompletedFrame>
) : Line

interface Frame {
    val score: Score?
        get() = TODO("Not yet implemented")}


interface PlayableFrame : Frame {
    fun roll(pinCount: PinCount): Frame
}

interface CompletedFrame : Frame

class UnplayedFrame : PlayableFrame {
    override fun roll(pinCount: PinCount) = when {
        pinCount.value == 10 -> Strike()
        else -> InProgressFrame(pinCount)
    }
}

class InProgressFrame(val roll1: PinCount) : PlayableFrame {
    override fun roll(pinCount: PinCount) = NormalCompletedFrame(roll1, pinCount)
}

class Strike : CompletedFrame {
    val roll1: PinCount = PinCount(10)
}

class NormalCompletedFrame(
    val roll1: PinCount,
    val roll2: PinCount
) : CompletedFrame


@JvmInline
value class PlayerName(val value: String)

@JvmInline
value class Score(val value: Int) {
    init { require(value in 0..300) }
}

@JvmInline
value class PinCount(val value: Int) {
    init { require(value in 0..10) }
}

private fun <E> List<E>.replace(currentLine: E, newLine: E): List<E> {
    TODO("Not yet implemented")
}

class Player
class Foul


