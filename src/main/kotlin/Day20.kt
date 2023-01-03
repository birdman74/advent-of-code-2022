//private const val FILENAME: String = "day20-sample.txt"
private const val FILENAME: String = "day20.txt"

fun main() {
    println("Part 1:\n")
    doDay20(1, 1)
    println("\nPart 2:\n")
    doDay20(811589153, 10)
}

private fun doDay20(key: Long, mixes: Int) {
    val input = Common.readFile(FILENAME)

    val numEntries = input.size
    var entries: MutableList<Long> = mutableListOf<Long>()
    var processIndexList: MutableList<Int> = mutableListOf<Int>()

    input.forEachIndexed { index, s ->
        entries.add(s.toLong() * key)
        processIndexList.add(index)
    }

    for (mix in 1..mixes) {
        for (i in processIndexList.indices) {
            val oldIndex = processIndexList.indexOf(i)
            val mover = entries[oldIndex]
            val newIndex = newPosition(mover, oldIndex, numEntries)

            entries.removeAt(oldIndex)
            entries.add(newIndex, mover)

            val indexToMove = processIndexList.removeAt(oldIndex)
            processIndexList.add(newIndex, indexToMove)

//        println("$entries")
        }
//        println("$entries")
    }

    val sumOfCoordinates = listOf(numXPastZero(entries.toList(), 1000),
                                  numXPastZero(entries.toList(), 2000),
                                  numXPastZero(entries.toList(), 3000)).sum()

    println("Sum of coordinates: $sumOfCoordinates\n")
}

private fun numXPastZero(entries: List<Long>, x: Long): Long {
    return entries[(entries.indexOf(0) + x).mod(entries.size)]
}

private fun newPosition(entry: Long, currentIndex: Int, listSize: Int): Int {
    val retVal = (currentIndex + entry).mod(listSize - 1)
    return if (retVal == 0) {
        listSize - 1
    } else {
        retVal
    }
}
