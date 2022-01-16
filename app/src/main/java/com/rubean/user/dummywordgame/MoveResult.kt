package com.rubean.user.dummywordgame

enum class MoveStatus {
    ERROR_MORE_THAN_ONE_WORD,
    ERROR_REPEATED_WORD,
    ERROR_MISSTYPED,
    SUCCESS
}

data class MoveResult(val status: MoveStatus, val errorCause: List<String>)