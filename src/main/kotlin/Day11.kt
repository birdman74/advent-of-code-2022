//private const val FILENAME: String = "day11-sample.txt"
private const val FILENAME: String = "day11.txt"

val monkeys: MutableList<Monkey> = mutableListOf<Monkey>()
var magicQuotient: Long = 0

fun main() {
    part01()
    part02()
}

private fun part01() {
    println("Part 1:\n")
    parseMonkeySpecs()
    executeMonkeyBusiness(20)
}

private fun part02() {
    println("\nPart 2:\n")
    parseMonkeySpecs()
    for (monkey in monkeys) {
        monkey.relaxAfterInspection = false
    }
    executeMonkeyBusiness(10000)
}

private fun executeMonkeyBusiness(rounds: Int) {
    repeat(rounds) {
        completeRound()
    }
    monkeys.forEachIndexed { index, monkey ->
        println("Monkey[$index]: ${ monkey.items }")
    }

    val topInspectionCounts: List<Int> = monkeys.map { it.numInspections }.sortedDescending().subList(0, 2)
    println("Amount of monkey business: ${ topInspectionCounts[0].toLong() * topInspectionCounts[1].toLong() }")

    monkeys.clear()
    magicQuotient = 0
}

private fun parseMonkeySpecs() {
    val input = Common.readFile(FILENAME)

    val monkeySpecs = input.chunked(7)
    monkeySpecs.forEach { monkeySpec ->
        val newMonkey: Monkey = Monkey(
            monkeySpec[1].split(": ")[1].split(", ").map { it -> it.toLong() }.toMutableList(),
            monkeySpec[2].split(" = ")[1].split(" ")[1],
            monkeySpec[2].split(" = ")[1].split(" ")[2],
            monkeySpec[3].split(" ").last().toInt(),
            monkeySpec[4].split(" ").last().toInt(),
            monkeySpec[5].split(" ").last().toInt())

        monkeys.add(newMonkey)
        if (magicQuotient == 0L) {
            magicQuotient = newMonkey.destinationDivisor.toLong()
        } else {
            magicQuotient *= newMonkey.destinationDivisor.toLong()
        }
    }
}

private fun completeRound() {
    for (monkey in monkeys) {
        monkey.inspectItems()
    }
}

/*
    1) Monkey Inspects Item
    2) Execute worry operation
    3) Worry level divided by 3 (rounded down to nearest int)
    4) Monkey throws item based on rule
 */

class Monkey(
    val items: MutableList<Long>,
    private val worryOperation: String,
    private val worryOperand: String,
    val destinationDivisor: Int,
    private val trueMonkeyIndex: Int,
    private val falseMonkeyIndex: Int) {

    var numInspections: Int = 0
    var relaxationDivisor: Int = 3
    var relaxAfterInspection: Boolean = true

    fun inspectItems() {
        items.forEach { oldVal ->
            numInspections += 1

            // Inspection / Worry Step
            var newVal: Long = 0
            val operand: Long = if (worryOperand == "old") oldVal else worryOperand.toLong()
            when (worryOperation) {
                "*" -> newVal = oldVal * operand
                "+" -> newVal = oldVal + operand
            }

            // Relaxation step
            if (relaxAfterInspection) {
                newVal = Math.floorDiv(newVal, relaxationDivisor)
            }

            // Lower concern
            if (newVal > magicQuotient) {
                newVal = newVal.mod(magicQuotient)
            }

            // Destination eval
            if (newVal.mod(destinationDivisor) == 0) {
                monkeys[trueMonkeyIndex].items.add(newVal)
            } else {
                monkeys[falseMonkeyIndex].items.add(newVal)
            }
        }
        items.clear()
    }
}