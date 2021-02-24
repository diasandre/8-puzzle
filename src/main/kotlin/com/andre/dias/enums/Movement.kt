package com.andre.dias.enums

enum class Movement(
    val x: Int,
    val y: Int
) {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, -1),
    DOWN(0, 1)

}