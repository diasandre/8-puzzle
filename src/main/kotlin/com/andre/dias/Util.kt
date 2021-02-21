package com.andre.dias

class Util {
    companion object {
        fun State.show() {
            val (one, two, three) = actualState
            println("${one[0]} ${one[1]} ${one[2]}")
            println("${two[0]} ${two[1]} ${two[2]}")
            println("${three[0]} ${three[1]} ${three[2]}")

            println("--------------------------------")
        }
    }
}