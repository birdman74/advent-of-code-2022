private const val LOWERCASE_CONVERSION = 'a'.code
private const val UPPERCASE_CONVERSION = 'A'.code

fun main() {
    part01()
    part02()
}

private fun part01() {
    println("Part 1:\n")
    val input = Common.readFile("day03-part01.txt")

    var prioritySum : Int = 0
    input.forEach {
        val contents = it.chunked(it.length / 2)
//        println("Rucksack contents: $contents")
        val commonItem: Int = contents[0].toCharArray().first { ch -> contents[1].contains(ch, false) }.code
        prioritySum += convertPriority(commonItem)
    }
    println("Common item priority sum: $prioritySum\n")
}

private fun part02() {
    println("Part 2:\n")

    val input = Common.readFile("day03-part01.txt")
    var prioritySum : Int = 0
    val groups = input.chunked(3)
    groups.forEach { group ->
        val commonItem: Int = group[0].toCharArray()
                                      .filter { ch -> group[1].contains(ch, false) }
                                      .first { ch2 -> group[2].contains(ch2, false) }.code

        prioritySum += convertPriority(commonItem)
    }
    println("Badge priority sum: $prioritySum")
}

private fun convertPriority (priority: Int) : Int {
    return if (priority >= LOWERCASE_CONVERSION) {
        priority - LOWERCASE_CONVERSION + 1
    } else {
        priority - UPPERCASE_CONVERSION + 27
    }
}