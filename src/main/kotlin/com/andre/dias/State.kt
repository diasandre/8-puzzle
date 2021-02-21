package com.andre.dias

data class State(val actualState: PuzzleList, val correctPositions: Int, val allStates: List<State>) {
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