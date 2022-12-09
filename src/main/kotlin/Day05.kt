import java.lang.Exception

//private const val FILENAME: String = "day05-sample.txt"
private const val FILENAME: String = "day05-part01.txt"

fun main() {
    val input = Common.readFile(FILENAME)

    val crates = input.subList(0, input.indexOf("")).reversed()
    val moves = input.subList(input.indexOf("") + 1, input.size)

    var stacks: MutableList<ArrayDeque<Char>> = mutableListOf<ArrayDeque<Char>>()
    crates[0].forEachIndexed { stackLocation, c ->
        if (c.isDigit()) {
            val stackIndex: Int = c.digitToInt()
            if (stacks.size < stackIndex) {
                stacks.add(ArrayDeque(0))
            }
            for (stackLevel in 1 until crates.size) {
                try {
                    val crate: Char = crates[stackLevel][stackLocation]
                    if (crate != ' ') {
                        stacks[stackIndex - 1].add(crate)
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    // Part 1
//    println("Part 1:\n")
//    moves.forEach { move ->
//        val parts: List<String> = move.split(" ")
//        val count: Int = parts[1].toInt()
//        val from: Int = parts[3].toInt()
//        val to: Int = parts[5].toInt()
//
//        for (i in 1..count) {
//            stacks[to - 1].add(stacks[from - 1].removeLast())
//        }

    // Part 2
    println("Part 2:\n")
    moves.forEach { move ->
        val parts: List<String> = move.split(" ")
        val count: Int = parts[1].toInt()
        val from: Int = parts[3].toInt()
        val to: Int = parts[5].toInt()

        stacks[to - 1].addAll(stacks[from - 1].takeLast(count))
        for (i in 1..count) {
            stacks[from - 1].removeLast()
        }
    }

    println("Stack Tops: ${ stacks.map { stack -> stack.takeLast(1) } }")
}
