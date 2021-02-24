package com.andre.dias.model

import com.andre.dias.PuzzleList

data class State(
    val actualState: PuzzleList,
    val cost: Int,
    val allStates: List<State> = mutableListOf()
) {
    override fun toString(): String {
        return actualState.map {
            it.map { value ->
                when (value) {
                    null -> "null"
                    else -> value.toString()
                }
            }.unify()
        }.unify()
    }

    private fun List<String>.unify() = reduce { accumulator, value -> "$accumulator$value" }
}