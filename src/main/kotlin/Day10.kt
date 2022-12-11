import kotlin.system.exitProcess

//private const val FILENAME: String = "day10-sample.txt"
private const val FILENAME: String = "day10.txt"

fun main() {
    println("Part 1:\n")
    val input = Common.readFile(FILENAME)
    var signalStrengthSum: Int = 0
    val keySignals: List<Int> = listOf(20, 60, 100, 140, 180, 220)
    var keySignalIndex: Int = 0
    var x: Int = 1
    var cyclesCompleted: Int = 0

    input.forEachIndexed { i, instruction ->
        var xBump: Int = 0
        var cyclesInInstruction: Int = 1

        if (instruction != "noop") {
            xBump = instruction.split(" ")[1].toInt()
            cyclesInInstruction = 2
        }
        repeat(cyclesInInstruction) {
            cyclesCompleted += 1
            drawPixel(cyclesCompleted, x)
        }

        if (keySignalIndex < keySignals.size && cyclesCompleted >= keySignals[keySignalIndex]) {
            signalStrengthSum += keySignals[keySignalIndex] * x
            keySignalIndex += 1
        }

        x += xBump
    }
    println("\nSum of key signal strengths: $signalStrengthSum\n")

}

private const val SCREEN_WIDTH: Int = 40
private fun drawPixel(cycle: Int, spriteCenter: Int) {
    var pixelPos: Int = (cycle - 1).mod(SCREEN_WIDTH)
    if (pixelPos in (spriteCenter - 1)..(spriteCenter + 1)) {
        print("#")
    } else {
        print(".")
    }
    if (pixelPos == SCREEN_WIDTH - 1) {
        println()
    }
}
