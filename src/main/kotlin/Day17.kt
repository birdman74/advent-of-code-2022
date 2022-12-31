private const val FILENAME: String = "day17-sample.txt"
//private const val FILENAME: String = "day17.txt"

private const val SHAFT_WIDTH: Int = 7
private const val ROCK: Char = '#'
private const val SPACE: Char = '.'
private const val LEFT: Char = '<'

fun main() {
    println("Part 1:\n")
    val windMovement = Common.readFile(FILENAME)[0]

    var windIndex: Int = 0
    val shapes = listOf(::HLine, ::Cross, ::LShape, ::VLine, ::Box)
    var shapeIndex: Int = 0
    val lastShapeIndex: Int = 2022
    val shaft: MutableList<String> = mutableListOf<String>(ROCK.toString().repeat(SHAFT_WIDTH))

    for (i in 1..lastShapeIndex) {
        val highestRock: Int = List(shaft.filterIndexed { _, s -> s.contains(ROCK) }.size) { index -> index }.max()

        // add space for shape to fall
        for (j in shaft.size..highestRock + 4) {
            shaft.add(SPACE.toString().repeat(SHAFT_WIDTH))
        }

        // create shape
        val currentShape: RockShape = shapes[shapeIndex]()
        currentShape.x = 2
        currentShape.y = highestRock + 4

        if (windMovement[windIndex] == LEFT) {
            currentShape.moveLeft(shaft)
        } else {
            currentShape.moveRight(shaft)
        }
        windIndex = (windIndex + 1).mod(windMovement.length)

        while(currentShape.drop(shaft)) {
            if (windMovement[windIndex] == LEFT) {
                currentShape.moveLeft(shaft)
            } else {
                currentShape.moveRight(shaft)
            }
            windIndex = (windIndex + 1).mod(windMovement.length)
        }

        placeRock(shaft, currentShape)

        shapeIndex = (shapeIndex + 1).mod(shapes.size)

        if (shapeIndex == 0) {
            val shaftHeight = List(shaft.filterIndexed { _, s -> s.contains(ROCK) }.size) { index -> index }.max()

            println("Height: $shaftHeight, Stone $i, windIndex: $windIndex")
        }
    }

    println("Tower Height: ${ List(shaft.filterIndexed { _, s -> s.contains(ROCK) }.size) { index -> index }.max() }\n")

    /*
    00 / 24 / 12 / 02 / 28 / 15 / 05 / 34 / 21 / 10 /
                   02 / 28 / 15 / 05 / 34 / 21 / 10 /
                   02 -> ..####. 25  78 131 - 15
                   28 -> .####.. 36  89 142 - 20
                   15 -> ..####. 43  96 149 - 25
                   05 -> #...... 51 104 157 - 30
                   34 -> .####.. 60 113 166 - 35
                   21 -> ..####. 66 119 172 - 40
                   10 -> ..####. 72 125 178 - 45

                   1,000,000,000,000 - 15 =
                   999,999,999,985 / (50 - 15) =
                   999,999,999,985 / 35 =
                   28,571,428,571 * 53 =
                   1,514,285,714,263 + 25 =
                   1,514,285,714,288

                   1,000,000,000,000 - 65 =
                   999,999,999,935 / (1775 - 65) =
                   999,999,999,935 / (1710) =
                   584,795,321 + remainder
                   584,795,321 * 1710 = 999,999,998,910
                   999,999,999,935 - 999,999,998,910 = 1025

                   107 + ((2754 - 107) * 584,795,321) =
                   107 + (2647 * 584,795,321) = 1,547,953,214,794
                   1,547,953,214,794 + (1706 - 107) =
                   1,547,953,214,794 + 1599 =
                   1,547,953,216,393


     */
}


private fun placeRock(shaft: MutableList<String>, rock: RockShape) {
    rock.massLocations.forEach {location ->
        val x = rock.x + location[0]
        val y = rock.y + location[1]
        shaft[y] = shaft[y].substring(0, x) + ROCK + shaft[y].substring(x + 1)
    }
}

private interface IRockShape {
    var x: Int
    var y: Int
    fun moveRight(shaft: MutableList<String>)
    fun moveLeft(shaft: MutableList<String>)
    fun drop(shaft: MutableList<String>) : Boolean
}

private open class RockShape(val massLocations: List<List<Int>>): IRockShape {
    override var x: Int = Int.MAX_VALUE
    override var y: Int = Int.MAX_VALUE

    override fun moveRight(shaft: MutableList<String>) {
        massLocations.forEach {location ->
            val newX = x + location[0] + 1
            val yLocation = y + location[1]
            if (newX == SHAFT_WIDTH || (yLocation < shaft.size && shaft[yLocation][newX] == ROCK)) {
                return
            }
        }
        x += 1
    }

    override fun moveLeft(shaft: MutableList<String>) {
        massLocations.forEach {location ->
            val newX = x + location[0] - 1
            val yLocation = y + location[1]
            if (newX < 0 || (yLocation < shaft.size && shaft[yLocation][newX] == ROCK)) {
                return
            }
        }
        x -= 1
    }

    override fun drop(shaft: MutableList<String>) : Boolean {
        massLocations.forEach {location ->
            val newY = y + location[1] - 1
            val xLocation = x + location[0]
            if (newY < shaft.size && shaft[newY][xLocation] == ROCK) {
                return false
            }
        }
        y -= 1
        return true
    }
}

private class HLine() : RockShape(listOf(listOf(0, 0), listOf(1, 0), listOf(2, 0), listOf(3, 0))) {
}

private class Cross() : RockShape(listOf(listOf(1, 0), listOf(0, 1), listOf(1, 1), listOf(2, 1), listOf(1, 2))) {
}

private class LShape() : RockShape(listOf(listOf(0, 0), listOf(1, 0), listOf(2, 0), listOf(2, 1), listOf(2, 2))) {
}

private class VLine() : RockShape(listOf(listOf(0, 0), listOf(0, 1), listOf(0, 2), listOf(0, 3))) {
}

private class Box() : RockShape(listOf(listOf(0, 0), listOf(1, 0), listOf(0, 1), listOf(1, 1))) {
}

