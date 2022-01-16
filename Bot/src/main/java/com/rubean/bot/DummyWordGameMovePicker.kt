package com.rubean.bot

const val BOT_MISTAKE_MOVE = "TOO_MUCH_FOR_ME"
const val BOT_MISTAKE_PROBABILITY = 3

class DummyWordGameMovePicker {

    private val wordsUsedSoFar = mutableListOf<String>()
    private var myLastWord = ""

    fun decideNextMove(wordFromHumanPlayer: String, shouldMakeRandomMistake: Boolean = true): String {
        if (shouldMakeRandomMistake) {
            val randomNumber = (0..100).random()
            if (randomNumber < BOT_MISTAKE_PROBABILITY) {
                return BOT_MISTAKE_MOVE
            }
        }

        wordsUsedSoFar.add(wordFromHumanPlayer)

        val nextWord = if (wordFromHumanPlayer.length > myLastWord.length) {
            "${wordFromHumanPlayer}a"
        } else {
            "${myLastWord}a"
        }

        myLastWord = nextWord
        wordsUsedSoFar.add(nextWord)
        return wordsUsedSoFar.joinToString(separator = " ")
    }
}