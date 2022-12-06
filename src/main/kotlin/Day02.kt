fun main() {
    part01()
    part02()
}

private fun part01() {
    println("Part 1:\n")
    val input = Common.readFile("day02-part01.txt")

    // OPPONENT: A = ROCK, B = PAPER, C = SCISSORS
    // ME      : X = ROCK, Y = PAPER, Z = SCISSORS
    // POINTS  : 1         2          3
    // POINTS/OUTCOME: WIN = 6, DRAW = 3, LOSS = 0

    val outcomes: Map<String, Int> = mapOf(
        "A X" to 4,
        "B X" to 1,
        "C X" to 7,
        "A Y" to 8,
        "B Y" to 5,
        "C Y" to 2,
        "A Z" to 3,
        "B Z" to 9,
        "C Z" to 6
    )

    println("Total Score: ${input.map { outcomes[it] }.filterIsInstance<Int>().sum() }\n\n")
}

private fun part02() {
    println("Part 2:\n")

    val input = Common.readFile("day02-part01.txt")

    // OPPONENT: A = ROCK, B = PAPER, C = SCISSORS
    // OUTCOME : X = LOSS, Y = DRAW,  Z = WIN
    // POINTS  : ROCK = 1, PAPER = 2, SCISSORS = 3
    // POINTS/OUTCOME: WIN = 6, DRAW = 3, LOSS = 0

    val outcomes: Map<String, Int> = mapOf(
        "A X" to 3,
        "B X" to 1,
        "C X" to 2,
        "A Y" to 4,
        "B Y" to 5,
        "C Y" to 6,
        "A Z" to 8,
        "B Z" to 9,
        "C Z" to 7
    )

    println("Total Score: ${input.map { outcomes[it] }.filterIsInstance<Int>().sum() }\n\n")
}