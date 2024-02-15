package com.oneeyedmen.bowling

sealed interface Game {
    val lines: List<Line>

    companion object {
        operator fun invoke(
            vararg playerNames: String,
            frameCount: Int = 10
        ): Game =
            when {
                playerNames.isEmpty() -> CompletedGame(emptyList())
                frameCount == 0 -> CompletedGame(
                    playerNames.map {
                        CompletedLine(it, emptyList())
                    }
                )
                else -> PlayableGame(*playerNames, frameCount = frameCount)
            }
    }
}

data class PlayableGame(
    override val lines: List<Line>
) : Game {
    constructor(vararg playerNames: String, frameCount: Int) : this(
        Unit.run {
            require(frameCount >= 1) {
                "Cannot construct a playable game with $frameCount frames"
            }
            playerNames.map {
                PlayableLine(
                    it,
                    List(frameCount) { index ->
                        if (index == frameCount -1 )
                            UnplayedFinalFrame()
                        else UnplayedFrame()
                    }
                )
            }
        }
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

data class CompletedGame(
    override val lines: List<CompletedLine>
) : Game