import Common.Companion.indexesOf

fun main() {
    val input = Common.readFile("day01-part01.txt")

    val blankLines : MutableList<Int> = input.indexesOf("").toMutableList()
    blankLines.add(0, -1)
    blankLines.add(input.size)

    val calorieTotals : MutableList<Int> = ArrayList()
    for (i in blankLines.indices) {
        if (i < blankLines.size - 1)
            calorieTotals.add(input.subList(blankLines[i] + 1, blankLines[i + 1]).map(String::toInt).sum())
    }
//    println("Calorie Totals: $calorieTotals\n")
    println("Most Calories Carried By Single Elf: ${calorieTotals.max()}")
    println("Calories Carried By Top Three Elves: ${calorieTotals.sortedDescending().subList(0, 3).sum()}")
}


