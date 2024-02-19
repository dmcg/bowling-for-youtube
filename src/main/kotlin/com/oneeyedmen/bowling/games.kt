package com.oneeyedmen.bowling

sealed interface Game {
    val lines: List<Line>

    companion object
}

operator fun Game.Companion.invoke(
    vararg playerNames: String,
    frameCount: Int = 10
): Game =
    when {
        playerNames.isNotEmpty() && frameCount != 0 ->
            PlayableGame(*playerNames, frameCount = frameCount)

        else -> CompletedGame(
            playerNames.map {
                CompletedLine(it, emptyList())
            }
        )
    }

class PlayableGame(
    override val lines: List<Line>
) : Game {
    constructor(vararg playerNames: String, frameCount: Int) : this(
        playerNames.map { PlayableLine(it, frameCount) }
    )

    init {
        require(lines.isNotEmpty())
        require(lines.any { it is PlayableLine })
    }

    val currentPlayer: String get() = currentLine.playerName

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

    private val currentLine: PlayableLine
        get() {
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

class CompletedGame(
    override val lines: List<CompletedLine>
) : Game