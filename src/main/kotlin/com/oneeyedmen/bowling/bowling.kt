package com.oneeyedmen.bowling

sealed interface Game {
    val lines: List<Line>
    companion object {
        operator fun invoke(vararg playerNames: String, frameCount: Int = 10): Game =
            when {
                playerNames.isEmpty() -> CompletedGame(emptyList())
                frameCount == 0 -> CompletedGame(playerNames.map { CompletedLine(it, emptyList()) })
                else -> PlayableGame(*playerNames, frameCount = frameCount)
            }
    }
}

data class PlayableGame(override val lines: List<Line>) : Game {

    val currentPlayer: String get() = currentLine.playerName

    constructor(vararg playerNames: String, frameCount: Int) : this(
        Unit.run {
            require(frameCount >= 1) { "Cannot construct a playable game with $frameCount frames"}
            playerNames.map {
                PlayableLine(
                    it,
                    List(frameCount) { UnplayedFrame() }
                )
            }
        }
    )

    init {
        require(lines.isNotEmpty())
        require(lines.any { it is PlayableLine })
    }
    fun roll(pinCount: PinCount): Game {
        val currentLine: PlayableLine = this.currentLine
        val newLine: Line = currentLine.roll(pinCount)
        val newLines: List<Line> = lines.replacing(currentLine, newLine)
        val completedLines: List<CompletedLine> = newLines.filterIsInstance<CompletedLine>()
        return if (completedLines.size == lines.size)
            CompletedGame(completedLines)
        else
            PlayableGame(newLines)
    }

    private val currentLine: PlayableLine get() {
        val indicesOfFirstPlayableFrame: List<Int> = lines.map { line ->
            line.frames.indexOfFirst { it is PlayableFrame }
        }
        val indexOfFirstPlayableFrame = indicesOfFirstPlayableFrame.minBy {
            if (it == -1) Int.MAX_VALUE else it
        }
        val indexOfFirstPlayableLine = indicesOfFirstPlayableFrame.indexOfFirst {
            it == indexOfFirstPlayableFrame
        }
        return lines[indexOfFirstPlayableLine] as? PlayableLine
            ?: error("programmer error")
    }
}
data class CompletedGame(
    override val lines: List<CompletedLine>
) : Game

sealed interface Line {
    val playerName: String
    val frames: List<Frame>
}
class PlayableLine(
    override val playerName: String,
    override val frames: List<Frame>
) : Line {
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

sealed interface Frame {
    val score: Score?
        get() = TODO("Not yet implemented")}


sealed interface PlayableFrame : Frame {
    fun roll(pinCount: PinCount): Frame
}

sealed interface CompletedFrame : Frame

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
) : CompletedFrame {
    val isSpare: Boolean get() = roll1.value + roll2.value == 10
}

@JvmInline
value class Score(val value: Int) {
    init { require(value in 0..300) }
}

@JvmInline
value class PinCount(val value: Int) {
    init { require(value in 0..10) }

    override fun toString(): String = when (value) {
        0 -> "-"
        else -> value.toString()
    }
}

private fun <E> List<E>.replacing(item: E, newItem: E): List<E> =
    map {if (it == item) newItem else it
}

@JvmInline
value class PlayerName(val value: String)

class Player
class Foul


