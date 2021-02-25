package com.andre.dias

import arrow.syntax.function.invoke
import com.andre.dias.enums.Algorithm
import com.andre.dias.enums.Algorithm.A_HEURISTIC_BEST
import com.andre.dias.enums.Algorithm.A_HEURISTIC_SIMPLE
import com.andre.dias.enums.Algorithm.UNIFORM_COST
import com.andre.dias.enums.Movement.DOWN
import com.andre.dias.enums.Movement.LEFT
import com.andre.dias.enums.Movement.RIGHT
import com.andre.dias.enums.Movement.UP
import com.andre.dias.util.StringUtil.Companion.show
import com.andre.dias.enums.Movement
import com.andre.dias.model.State
import kotlin.math.abs

typealias PuzzleList = List<MutableList<Int?>>

private const val CORRECT_POSITIONS_MAX = 9

class EightPuzzle(state: PuzzleList, algorithm: Algorithm) {

    private val initialState: PuzzleList = state

    private val SELECTED_ALGORITHM = algorithm

    private val goalState: PuzzleList = listOf(
        mutableListOf(1, 2, 3),
        mutableListOf(4, 5, 6),
        mutableListOf(7, 8, null)
    )

    private val exploredStates: MutableList<State> = mutableListOf()
    private var openStates: MutableList<State> =
        mutableListOf(
            State(
                initialState,
                calculateCost(initialState)
            )
        )

    private var maxPoints = 0
    private var maxFronteira = 0
    private var finalState: State? = null

    fun start() {
        println("iniciando a busca")
        while (maxPoints != CORRECT_POSITIONS_MAX) {
            val actualState = openStates.first()
            val (state, _, movements) = actualState

            val hasBeenExplored = exists(exploredStates, actualState)
            if (hasBeenExplored) {
                openStates.remove(actualState)
                continue
            }

            exploredStates.add(actualState)
            openStates.remove(actualState)

            checkFronteiraAndUpdate()

            val goal = isGoal(state)
            if (goal) {
                finalState = actualState
                break
            }

            val (x, y) = findActualPosition(state)

            requireNotNull(x)
            requireNotNull(y)

            val availableMovements: List<Movement> = availableMovements(x, y)

            availableMovements.generateMovements(actualState, movements + actualState, x to y)

            openStates = openStates
                .sortedBy(State::cost)
                .toMutableList()
        }

        showResult()
    }

    private fun exists(list: List<State>, state: State) = list.any { es -> es.toString() == state.toString() }

    private fun checkFronteiraAndUpdate() {
        if (openStates.size > maxFronteira) maxFronteira = openStates.size
    }

    private fun isGoal(state: PuzzleList): Boolean {
        maxPoints = CORRECT_POSITIONS_MAX - incorrectPositions(state)
        return maxPoints == CORRECT_POSITIONS_MAX
    }

    private fun findActualPosition(state: PuzzleList, of: Int? = null): Pair<Int?, Int?> {
        state.forEachIndexed { index, row ->
            row.forEachIndexed { indexRow, item ->
                if (item == of) return Pair(indexRow, index)
            }
        }
        return Pair(null, null)
    }

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

    private fun List<Movement>.generateMovements(
        state: State, pastMovements: List<State>, actualEmptyPosition: Pair<Int, Int>
    ) {
        this.map { movement -> movement.applyMovement(state, actualEmptyPosition, pastMovements) }
            .filterNot((::exists)(openStates))
            .forEach(openStates::add)
    }

    private fun Movement.applyMovement(
        state: State,
        actualEmptyPosition: Pair<Int, Int>,
        pastMovements: List<State>
    ): State {
        val (actualState, _, allStates) = state
        val newState = actualState.copy()

        val (x, y) = actualEmptyPosition

        val newX = x + this.x
        val newY = y + this.y

        val actualPosition = newState[y][x]
        val newPosition = newState[newY][newX]

        newState[y][x] = newPosition
        newState[newY][newX] = actualPosition

        return State(
            newState,
            calculateCost(newState, allStates.size),
            pastMovements
        )
    }

    private fun calculateCost(state: PuzzleList, lastMovements: Int = 0): Int = when (SELECTED_ALGORITHM) {
        UNIFORM_COST -> lastMovements + 1
        A_HEURISTIC_SIMPLE -> lastMovements + 1 + incorrectPositions(state)
        A_HEURISTIC_BEST -> lastMovements + 1 + heuristicBest(state)
    }

    private fun incorrectPositions(state: PuzzleList) =
        state.mapIndexed { y, row ->
            val rowGoal = goalState[y]
            row.mapIndexed { x, item ->
                if (rowGoal[x] == item) 0 else 1
            }.sum()
        }.sum()

    private fun heuristicBest(state: PuzzleList): Int {
        return state.mapIndexed { y, row ->
            val rowGoal = goalState[y]
            row.mapIndexed { x, item ->
                if (rowGoal[x] == item) 0 else {
                    val value = state[y][x]
                    val (xExcepted, yExpected) = findActualPosition(goalState, value)

                    requireNotNull(xExcepted)
                    requireNotNull(yExpected)

                    abs(xExcepted - x) + abs(yExpected - y)
                }
            }.sum()
        }.sum()
    }

    private fun showResult() {
        requireNotNull(finalState)

        finalState?.allStates?.forEach {
            it.show()
        }

        finalState?.show()

        println("O total de nodos visitados: ${exploredStates.size}")
        println("O total de nodos expandidos/criados: ${openStates.size}")
        println("O maior tamanho da fronteira durante a busca: $maxFronteira")
        println("O tamanho do caminho: ${finalState?.allStates?.size} ")
    }

    private fun PuzzleList.copy() = toMutableList().map(List<Int?>::toMutableList)

}