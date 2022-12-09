//private const val FILENAME: String = "day04-sample.txt"
private const val FILENAME: String = "day04-part01.txt"

fun main() {
    val input = Common.readFile(FILENAME)

    var totalOverlapCount: Int = 0
    var overlapCount: Int = 0

    input.forEach {line ->
        val sections: List<List<Int>> = line.split(",").map { section -> section.split("-").map { it.toInt() } }
        val first = (sections[0][0]..sections[0][1]).toList()
        val second = (sections[1][0]..sections[1][1]).toList()

        if (first.containsAll(second) || second.containsAll(first)) {
            totalOverlapCount += 1
        }

        if (first.any { section -> second.contains(section) } ||
            second.any { section -> first.contains(section) }) {
            overlapCount += 1
        }
    }

    println("Part 1:\n")
    println("Total Overlap Instances: $totalOverlapCount\n")
    println("Part 2:\n")
    println("Overlap Instances: $overlapCount")
}
