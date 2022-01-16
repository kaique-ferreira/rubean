package com.rubean.user.dummywordgame

import android.content.Context
import com.rubean.user.botconnection.BotConnection

private const val TURNS_LIMIT = 100

class DummyWordGameModerator(
    private val humanWon: (MoveResult) -> Unit,
    private val botWon: (MoveResult) -> Unit,
    private val botHasMoved: (String) -> Unit,
    private val draw: () -> Unit
) : BotMoveListener {

    private var turnsCount = 0
    private val botConnection = BotConnection()
    val previouslyPlayedWords = mutableListOf<String>()

    fun start(context: Context) {
        botConnection.start(context, this)
    }

    fun stop(context: Context) {
        botConnection.stop(context)
    }

    fun handleHumanPlayerInput(input: String) {
        val moveResult = checkPlayerInput(input)

        if (moveResult.status == MoveStatus.SUCCESS) {
            if (isAboveTurnsLimit()) {
                endGame()
                draw()
                return
            }

            botConnection.sendWord(previouslyPlayedWords.last())
        } else {
            endGame()
            botWon(moveResult)
        }
    }

    override fun handleBotPlayerInput(input: String) {
        val moveResult = checkPlayerInput(input)

        if (moveResult.status == MoveStatus.SUCCESS) {
            if (isAboveTurnsLimit()) {
                endGame()
                draw()
                return
            }

            botHasMoved(input)
        } else {
            endGame()
            humanWon(moveResult)
        }
    }

    private fun isAboveTurnsLimit(): Boolean {
        if (++turnsCount > TURNS_LIMIT) {
            return true
        }

        return false
    }

    private fun endGame() {
        turnsCount = 0
        previouslyPlayedWords.clear()
        botConnection.tellBotGameIsOver()
    }

    private fun checkPlayerInput(input: String): MoveResult {
        val playerWords = convertTextInputIntoWordsList((input))
        val lastWord = playerWords.last()

        if (previouslyPlayedWords.contains(lastWord)) {
            return MoveResult(
                status = MoveStatus.ERROR_REPEATED_WORD,
                errorCause = listOf(lastWord)
            )
        }

        if (playerWords.size > previouslyPlayedWords.size + 1) {
            return MoveResult(
                status = MoveStatus.ERROR_MORE_THAN_ONE_WORD,
                errorCause = listOf(lastWord)
            )
        }

        previouslyPlayedWords.forEachIndexed { index, word ->
            val playerWord = if (index < playerWords.size) {
                playerWords[index]
            } else " "

            if (playerWord != word) {
                return MoveResult(
                    status = MoveStatus.ERROR_MISSTYPED,
                    errorCause = listOf(playerWord, word)
                )
            }
        }

        previouslyPlayedWords.add(playerWords.last())

        return MoveResult(status = MoveStatus.SUCCESS, errorCause = emptyList())
    }

    private fun convertTextInputIntoWordsList(input: String): List<String> {
        val trimmedInput = input.trim()
        val stringBuilder = StringBuilder()

        val playerWords = mutableListOf<String>()

        for (i in trimmedInput.indices) {
            val character = trimmedInput[i]
            if (!character.isWhitespace()) {
                stringBuilder.append(character)
            } else {
                playerWords.add(stringBuilder.toString())
                stringBuilder.clear()
            }
        }

        playerWords.add(stringBuilder.toString())

        return playerWords
    }
}

interface BotMoveListener {
    fun handleBotPlayerInput(input: String)
}