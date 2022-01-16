package com.rubean.bot

import org.junit.Test
import org.junit.Assert.*

class DummyWordGameMovePickerTest {

    @Test
    fun testBotMove() {
        val dummyWordGameMovePicker = DummyWordGameMovePicker()

        var nextMove = dummyWordGameMovePicker.decideNextMove("b", shouldMakeRandomMistake = false)
        assertEquals("b ba", nextMove)

        nextMove = dummyWordGameMovePicker.decideNextMove("aa", shouldMakeRandomMistake = false)
        assertEquals("b ba aa baa", nextMove)

        nextMove = dummyWordGameMovePicker.decideNextMove("stone" , shouldMakeRandomMistake = false)
        assertEquals("b ba aa baa stone stonea", nextMove)

        nextMove = dummyWordGameMovePicker.decideNextMove("“a\\/n”", shouldMakeRandomMistake = false)
        assertEquals("b ba aa baa stone stonea “a\\/n” stoneaa", nextMove)
    }
}