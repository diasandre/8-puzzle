package com.andre.dias

import arrow.syntax.function.invoke
import com.andre.dias.Movement.DOWN
import com.andre.dias.Movement.LEFT
import com.andre.dias.Movement.RIGHT
import com.andre.dias.Movement.UP
import com.andre.dias.Util.Companion.show

typealias PuzzleList = List<MutableList<Int?>>

class EightPuzzle {
    private val CORRECT_POSITIONS_MAX = 9
    private var maxPoints = 0

    private val initialState: PuzzleList = listOf(
        mutableListOf(8, 7, 1),
        mutableListOf(6, null, 2),
        mutableListOf(5, 4, 3)
    )

    private val goalState: PuzzleList = listOf(
        mutableListOf(1, 2, 3),
        mutableListOf(4, 5, 6),
        mutableListOf(7, 8, null)
    )

    private val exploredStates: MutableList<State> = mutableListOf()
    private var openStates: MutableList<State> =
        mutableListOf(State(initialState, calculateCost(initialState), mutableListOf()))

    private fun availableMovements(x: Int, y: Int): List<Movement> = when {
        x == 0 && y == 0 -> listOf(RIGHT, DOWN)
        x == 1 && y == 0 -> listOf(LEFT, RIGHT, DOWN)
        x == 2 && y == 0 -> listOf(LEFT, DOWN)
        x == 0 && y == 1 -> listOf(UP, RIGHT, DOWN)
        x == 1 && y == 1 -> listOf(UP, DOWN, RIGHT, LEFT)
        x == 2 && y == 1 -> listOf(UP, LEFT, DOWN)
        x == 0 && y == 2 -> listOf(UP, RIGHT)
        x == 1 && y == 2 -> listOf(UP, LEFT, RIGHT)
        x == 2 && y == 2 -> listOf(UP, LEFT)
        else -> throw Exception("unknown position")
    }

    private fun calculateCost(state: PuzzleList): Int {
        var points = 0

        state.forEachIndexed { index, row ->
            val rowGoal = goalState[index]
            row.forEachIndexed { indexRow, item ->
                if (rowGoal[indexRow] == item) points++
            }
        }

        maxPoints = points

        return points
    }

    private fun findActualPosition(state: PuzzleList): Pair<Int?, Int?> {
        state.forEachIndexed { index, row ->
            row.forEachIndexed { indexRow, item ->
                if (item == null) return Pair(indexRow, index)
            }
        }
        return Pair(null, null)
    }

    private fun checkIfIsGoal(state: PuzzleList) {
        calculateCost(state)
    }

    private fun exists(list: List<State>, state: State) = list.any { es -> es.toString() == state.toString() }

    private fun Movement.applyMovement(newState: PuzzleList, actual: Pair<Int, Int>, movements: List<State>): State {
        val (x, y) = actual
        val newX = x + this.x
        val newY = y + this.y

        val actualPosition = newState[y][x]
        val newPosition = newState[newY][newX]

        newState[y][x] = newPosition
        newState[newY][newX] = actualPosition

        return State(newState, calculateCost(newState), movements)
    }

    fun start() {
        println("-----------------------------------");
        println("             8Puzzle               ");
        println("-----------------------------------");

        while (maxPoints != CORRECT_POSITIONS_MAX) {
            val actualState = openStates.first()
            val (state, _, movements) = actualState

            checkIfIsGoal(state)

            val hasBeenExplored = exists(exploredStates, actualState)
            if (hasBeenExplored) {
                openStates.remove(actualState)
                continue
            }

            val (x, y) = findActualPosition(state)

            requireNotNull(x)
            requireNotNull(y)

            val availableMovements: List<Movement> = availableMovements(x, y)

            availableMovements.generateMovements(movements + actualState, x to y, state)

            openStates.remove(actualState)
            exploredStates.add(actualState)

            openStates = openStates.sortedByDescending(State::correctPositions).toMutableList()
        }

        showResult()
    }

    private fun showResult() {
        val finalState = openStates.first()
        finalState.allStates.forEach {
            it.show()
        }

        finalState.show()

        println("O total de nodos visitados: ${exploredStates.size}")
        println("O total de nodos expandidos/criados: ${openStates.size + exploredStates.size}")
        println("O maior tamanho da fronteira durante a busca: ${openStates.size}")
        println("O tamanho do caminho: ${finalState.allStates.size} ")
    }

    private fun List<Movement>.generateMovements(newMovements: List<State>, pair: Pair<Int, Int>, state: PuzzleList) {
        this.map { movement -> movement.applyMovement(state.copy(), pair, newMovements) }
            .filterNot((::exists)(openStates))
            .forEach(openStates::add)
    }

    private fun PuzzleList.copy() = toMutableList().map(List<Int?>::toMutableList)

}