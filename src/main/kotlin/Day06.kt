import kotlin.math.sign

//private const val FILENAME01: String = "day06-part01-sample.txt"
private const val FILENAME01: String = "day06-part01.txt"
//private const val FILENAME02: String = "day06-part02-sample.txt"
private const val FILENAME02: String = "day06-part01.txt"

private fun main() {
    part01()
    part02()
}

private fun part01() {
    println("Part 1:\n")
    val signals = Common.readFile(FILENAME01)

    signals.forEach { signal ->
        println("End of packet header location: ${ startOfHeader(signal, 4)}")
    }
    println("");
}

private fun part02() {
    println("Part 2:\n")
    val signals = Common.readFile(FILENAME02)

    signals.forEach { signal ->
        println("End of message header location: ${ startOfHeader(signal, 14)}")
    }
    println("");
}

private fun startOfHeader(signal: String, headerLength: Int): Int {
    for (i in signal.indices.filter { it > headerLength - 2 }) {
        if (signal.substring(i - (headerLength - 1), i + 1).groupBy { it }.size == headerLength) {
            return i + 1
        }
    }
    return -1
}
