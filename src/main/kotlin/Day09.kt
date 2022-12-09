//private const val FILENAME: String = "day09-sample.txt"
private const val FILENAME: String = "day09.txt"

fun main() {
    println("Part 1:\n")
    runSim(2)
    println("Part 2:\n")
    runSim(10)
}

private fun runSim(knotCount: Int) {
    val moves = Common.readFile(FILENAME)

    val tailPositions: MutableList<List<Int>> = mutableListOf()

    val headPos: MutableList<Int> = mutableListOf(5000, 5000)
    val tailPos: MutableList<Int> = mutableListOf(5000, 5000)

    val knotPositions: MutableList<MutableList<Int>> = MutableList(knotCount) { mutableListOf(5000, 5000) }

    markVisited(tailPositions, knotPositions.last())

    moves.forEach { move ->
        val pieces: List<String> = move.split(" ")
        val dir = pieces[0]
        val steps = pieces[1].toInt()

        val transform: List<Int> = if (dir == "L") {
            listOf(0, -1)
        } else if (dir == "R") {
            listOf(0, 1)
        } else if (dir == "U") {
            listOf(-1, 0)
        } else {
            listOf(1, 0)
        }
        moveHead(tailPositions, knotPositions, transform, steps)

    }

    println("Field positions visited by tail: ${ tailPositions.distinct().size }\n")
}

private fun markVisited(tailPositions: MutableList<List<Int>>, pos: MutableList<Int>) {
    tailPositions.add(pos.toList())
}

private fun moveHead(tailPositions: MutableList<List<Int>>,
                     knotPositions: MutableList<MutableList<Int>>,
                     transform: List<Int>,
                     steps: Int) {
    for (i in 1..steps) {
        knotPositions[0][0] += transform[0]
        knotPositions[0][1] += transform[1]

        for (j in 1..knotPositions.size - 1) {
            moveNextKnot(knotPositions[j - 1], knotPositions[j])
        }

        markVisited(tailPositions, knotPositions.last())
    }
}

private fun moveNextKnot(headPos: MutableList<Int>, tailPos: MutableList<Int>) {
    if (headPos[0] == tailPos[0]) {
        if (headPos[1] > tailPos[1] + 1) {
            tailPos[1] += 1
        } else if (headPos[1] < tailPos[1] - 1) {
            tailPos[1] -= 1
        }
    } else if (headPos[1] == tailPos[1]) {
        if (headPos[0] > tailPos[0] + 1) {
            tailPos[0] += 1
        } else if (headPos[0] < tailPos[0] - 1) {
            tailPos[0] -= 1
        }
    } else {
        if (kotlin.math.abs(headPos[0] - tailPos[0]) > 1 ||
            kotlin.math.abs(headPos[1] - tailPos[1]) > 1) {

            if (headPos[1] > tailPos[1]) {
                tailPos[1] += 1
            } else if (headPos[1] < tailPos[1]) {
                tailPos[1] -= 1
            }
            if (headPos[0] > tailPos[0]) {
                tailPos[0] += 1
            } else if (headPos[0] < tailPos[0]) {
                tailPos[0] -= 1
            }
        }
    }
}

