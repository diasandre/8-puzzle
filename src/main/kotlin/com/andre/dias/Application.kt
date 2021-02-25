package com.andre.dias

import com.andre.dias.enums.Algorithm
import java.lang.Exception

fun main() {
    println("-----------------------------------")
    println("             8Puzzle               ")
    println("-----------------------------------")

    println("insira o estado inicial no formato: 123456780")

    val initialState = readLine()

    val firstRow = initialState?.substring(0, 3)?.map(::toRow)?.toMutableList() ?: mutableListOf()
    val secondRow = initialState?.substring(3, 6)?.map(::toRow)?.toMutableList() ?: mutableListOf()
    val thirdRow = initialState?.substring(6, 9)?.map(::toRow)?.toMutableList() ?: mutableListOf()

    if (firstRow.isEmpty() ||
        secondRow.isEmpty() ||
        thirdRow.isEmpty() ||
        firstRow.size < 3 ||
        secondRow.size < 3 ||
        thirdRow.size < 3
    ) throw Exception(
        "O estado está incorreto, tente novamente!"
    )

    val state = listOf(firstRow, secondRow, thirdRow)

    println("Escolha seu algoritmo:")
    println("1. Custo Uniforme")
    println("2. A* com uma heurística simples")
    println("3. A* com a heurística mais precisa que conseguirem")

    val algorithm = when (readLine()?.toInt()) {
        1 -> Algorithm.UNIFORM_COST
        2 -> Algorithm.A_HEURISTIC_SIMPLE
        3 -> Algorithm.A_HEURISTIC_BEST
        else -> throw Exception("escolha um dos algoritmos existentes")
    }

    println("dependendo do estado inicial, o algoritmo pode demorar bastante para achar uma solução")

    EightPuzzle(state, algorithm).start()
}

private fun toRow(item: Char): Int? = if (item.toString().toInt() == 0) {
    null
} else {
    item.toString().toInt()
}