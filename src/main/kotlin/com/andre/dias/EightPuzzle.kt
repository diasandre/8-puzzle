package com.andre.dias

import arrow.syntax.function.invoke
import com.andre.dias.ALGORITHM.A_HEURISTIC_BEST
import com.andre.dias.ALGORITHM.A_HEURISTIC_SIMPLE
import com.andre.dias.ALGORITHM.UNIFORM_COST
import com.andre.dias.Movement.DOWN
import com.andre.dias.Movement.LEFT
import com.andre.dias.Movement.RIGHT
import com.andre.dias.Movement.UP
import com.andre.dias.Util.Companion.show

typealias PuzzleList = List<MutableList<Int?>>

class EightPuzzle {
    private val CORRECT_POSITIONS_MAX = 9
    private val SELECTED_ALGORITHM = UNIFORM_COST

    private var maxPoints = 0
    private var maxFronteira = 0

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

    private fun isGoal(state: PuzzleList): Boolean {
        maxPoints = calculateCost(state)
        return maxPoints == CORRECT_POSITIONS_MAX
    }

    private fun calculateCost(state: PuzzleList): Int {
        return when (SELECTED_ALGORITHM) {
            UNIFORM_COST -> uniformCost(state)
            A_HEURISTIC_SIMPLE -> TODO()
            A_HEURISTIC_BEST -> TODO()
        }
    }

    private fun uniformCost(state: PuzzleList): Int {
        return state.mapIndexed { index, row ->
            val rowGoal = goalState[index]
            row.mapIndexed { indexRow, item ->
                if (rowGoal[indexRow] == item) 1 else 0
            }.sum()
        }.sum()
    }

    private fun findActualPosition(state: PuzzleList): Pair<Int?, Int?> {
        state.forEachIndexed { index, row ->
            row.forEachIndexed { indexRow, item ->
                if (item == null) return Pair(indexRow, index)
            }
        }
        return Pair(null, null)
    }

    private fun exists(list: List<State>, state: State) = list.any { es -> es.toString() == state.toString() }

    private fun Movement.applyMovement(
        newState: PuzzleList,
        actualEmptyPosition: Pair<Int, Int>,
        pastMovements: List<State>
    ): State {
        val (x, y) = actualEmptyPosition

        val newX = x + this.x
        val newY = y + this.y

        val actualPosition = newState[y][x]
        val newPosition = newState[newY][newX]

        newState[y][x] = newPosition
        newState[newY][newX] = actualPosition

        return State(newState, calculateCost(newState), pastMovements)
    }

    fun start() {
        println("-----------------------------------");
        println("             8Puzzle               ");
        println("-----------------------------------");

        while (maxPoints != CORRECT_POSITIONS_MAX) {
            val actualState = openStates.first()
            val (state, _, movements) = actualState

            val hasBeenExplored = exists(exploredStates, actualState)
            if (hasBeenExplored) {
                openStates.remove(actualState)
                continue
            }

            checkFronteiraAndUpdate()

            exploredStates.add(actualState)
            openStates.remove(actualState)

            val goal = isGoal(state)
            if (goal) break

            val (x, y) = findActualPosition(state)

            requireNotNull(x)
            requireNotNull(y)

            val availableMovements: List<Movement> = availableMovements(x, y)

            availableMovements.generateMovements(state, movements + actualState, x to y)

            openStates = openStates.sortedByDescending(State::cost).toMutableList()
        }

        showResult()
    }

    private fun checkFronteiraAndUpdate() {
        if (openStates.size > maxFronteira) maxFronteira = openStates.size
    }

    private fun showResult() {
        val finalState = openStates.first()
        finalState.allStates.forEach {
            it.show()
        }

        finalState.show()

        println("O total de nodos visitados: ${exploredStates.size}")
        println("O total de nodos expandidos/criados: ${openStates.size}")
        println("O maior tamanho da fronteira durante a busca: $maxFronteira")
        println("O tamanho do caminho: ${finalState.allStates.size} ")
    }

    private fun List<Movement>.generateMovements(
        state: PuzzleList,
        pastMovements: List<State>,
        actualEmptyPosition: Pair<Int, Int>
    ) {
        this.map { movement -> movement.applyMovement(state.copy(), actualEmptyPosition, pastMovements) }
            .filterNot((::exists)(openStates))
            .forEach(openStates::add)
    }

    private fun PuzzleList.copy() = toMutableList().map(List<Int?>::toMutableList)

}