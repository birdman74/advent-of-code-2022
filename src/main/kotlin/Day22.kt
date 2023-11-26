private const val FILENAME: String = "day22.txt"

private const val TURN_RIGHT = 'R'
private const val TURN_LEFT = 'L'
private const val WALL = '#'
private const val VOID = ' '

private const val FLAT_MAP = false

private var mapWidth: Int = 0
private var mapHeight: Int = 0

fun main() {
    part01()
}

private fun part01() {
    println("Part 1:\n")
    val input = Common.readFile(FILENAME)
    val map = parseMap(input)
    val instructions = parseInstructions(input)

    var position = startPosition(map)

    instructions.forEach { action ->
        position = moveForward(map, position, action.steps)
        position.direction = newDirection(position.direction, action.turn)
    }

    println("Password: ${calcPassword(position)}\n")
}

private fun calcPassword(position: Point22): Int {
    return (1000 * (position.y + 1)) + (4 * (position.x + 1)) + position.direction.directionValue
}

private fun moveForward(map: List<String>, position: Point22, steps: Int): Point22 {

    var oldPosition = position
    var newPosition = position

    for (i in 1..steps) {
        when (oldPosition.direction) {
            MapDirection.RIGHT -> {
                newPosition = Point22(oldPosition.x + 1, oldPosition.y, oldPosition.direction)
                if (newPosition.x >= mapWidth || charAtPosition(map, newPosition) == VOID) {
                    if (FLAT_MAP) {
                        newPosition.x = leftmostMapX(map[newPosition.y])
                    } else {
                        newPosition = threeDMove(oldPosition)
                    }
                }
            }

            MapDirection.DOWN -> {
                newPosition = Point22(oldPosition.x, oldPosition.y + 1, oldPosition.direction)
                if (newPosition.y >= mapHeight || charAtPosition(map, newPosition) == VOID) {
                    if (FLAT_MAP) {
                        newPosition.y = topmostMapY(map, newPosition.x)
                    } else {
                        newPosition = threeDMove(oldPosition)
                    }
                }
            }

            MapDirection.LEFT -> {
                newPosition = Point22(oldPosition.x - 1, oldPosition.y, oldPosition.direction)
                if (newPosition.x < 0 || charAtPosition(map, newPosition) == VOID) {
                    if (FLAT_MAP) {
                        newPosition.x = rightmostMapX(map[newPosition.y])
                    } else {
                        newPosition = threeDMove(oldPosition)
                    }
                }
            }

            MapDirection.UP -> {
                newPosition = Point22(oldPosition.x, oldPosition.y - 1, oldPosition.direction)
                if (newPosition.y < 0 || charAtPosition(map, newPosition) == VOID) {
                    if (FLAT_MAP) {
                        newPosition.y = bottommostMapY(map, newPosition.x)
                    } else {
                        newPosition = threeDMove(oldPosition)
                    }
                }
            }
        }
        if (charAtPosition(map, newPosition) == WALL) {
            return oldPosition
        } else {
            oldPosition = newPosition
        }
    }
    return newPosition
}

private fun threeDMove(position: Point22): Point22 {
    return when (Triple(position.x / 50, position.y / 50, position.direction)) {
        Triple(1, 0, MapDirection.LEFT) -> Point22(0, 149 - position.y, MapDirection.RIGHT)
        Triple(1, 0, MapDirection.UP) -> Point22(0, position.x + 100, MapDirection.RIGHT)
        Triple(2, 0, MapDirection.UP) -> Point22(position.x - 100, 199, MapDirection.UP)
        Triple(2, 0, MapDirection.RIGHT) -> Point22(99, 149 - position.y, MapDirection.LEFT)
        Triple(2, 0, MapDirection.DOWN) -> Point22(99, position.x - 50, MapDirection.LEFT)
        Triple(1, 1, MapDirection.LEFT) -> Point22(position.y - 50, 100, MapDirection.DOWN)
        Triple(1, 1, MapDirection.RIGHT) -> Point22(position.y + 50, 49, MapDirection.UP)
        Triple(0, 2, MapDirection.UP) -> Point22(50, position.x + 50, MapDirection.RIGHT)
        Triple(0, 2, MapDirection.LEFT) -> Point22(50, 149 - position.y, MapDirection.RIGHT)
        Triple(1, 2, MapDirection.RIGHT) -> Point22(149, 149 - position.y, MapDirection.LEFT)
        Triple(1, 2, MapDirection.DOWN) -> Point22(49, position.x + 100, MapDirection.LEFT)
        Triple(0, 3, MapDirection.LEFT) -> Point22(position.y - 100, 0, MapDirection.DOWN)
        Triple(0, 3, MapDirection.RIGHT) -> Point22(position.y - 100, 149, MapDirection.UP)
        Triple(0, 3, MapDirection.DOWN) -> Point22(position.x + 100, 0, MapDirection.DOWN)
        else -> error("Fail!")
    }
}

private fun charAtPosition(map: List<String>, position: Point22): Char {
    return map[position.y][position.x]
}

private fun bottommostMapY(map: List<String>, xPosition: Int): Int {
    return map.indexOfLast { row -> row[xPosition] != VOID }
}

private fun rightmostMapX(mapRow: String): Int {
    return mapRow.indexOfLast { c -> c != VOID }
}

private fun topmostMapY(map: List<String>, xPosition: Int): Int {
    return map.indexOfFirst { row -> row[xPosition] != VOID }
}

private fun leftmostMapX(mapRow: String): Int {
    return mapRow.indexOfFirst { c -> c != VOID }
}

private fun startPosition(map: List<String>): Point22 {
    return Point22(map[0].indexOfFirst { c -> c != VOID && c != WALL}, 0, MapDirection.RIGHT)
}

private fun calculateMapWidth(map: List<String>): Int {
    return map.maxOf { line -> line.length }
}

private fun calculateMapHeight(map: List<String>): Int {
    return map.size
}

private fun parseMap(input: List<String>): List<String> {
    val map: MutableList<String> = mutableListOf()

    for (line in input) {
        if (line.trim().isEmpty()) {
            break
        }
        map.add(line)
    }
    mapWidth = calculateMapWidth(map)
    mapHeight = calculateMapHeight(map)
    for (i in 0..map.size - 1) {
        map[i] = map[i].padEnd(mapWidth, VOID)
    }

    return map.toList()
}

private fun parseInstructions(input: List<String>): List<Instruction> {
    val instructions: MutableList<Instruction> = mutableListOf()

    for (i in input.size - 1 downTo 0) {
        if (input[i].trim().isNotEmpty()) {
            val rawInstructions = input[i]

            var steps = ""
            for(char in rawInstructions) {
                if (char == TURN_LEFT || char == TURN_RIGHT) {
                    instructions.add(Instruction(steps.toInt(), char))
                    steps = ""
                } else {
                    steps += char
                }
            }
            if(steps.isNotBlank()) {
                instructions.add(Instruction(steps.trim().toInt(), 'X'))
            }
            break
        }
    }
    return instructions
}

private class Instruction(val steps: Int, val turn: Char) {
    override fun toString(): String {
        return "Steps: ${steps}, turn: $turn"
    }
}

private fun newDirection(currentDirection: MapDirection, turn: Char): MapDirection {
    var newDirection = currentDirection
    if (turn == TURN_RIGHT || turn == TURN_LEFT) {
        when (currentDirection) {
            MapDirection.RIGHT -> {
                newDirection = when (turn) {
                    TURN_RIGHT -> MapDirection.DOWN
                    else -> MapDirection.UP
                }
            }

            MapDirection.DOWN -> {
                newDirection = when (turn) {
                    TURN_RIGHT -> MapDirection.LEFT
                    else -> MapDirection.RIGHT
                }
            }

            MapDirection.LEFT -> {
                newDirection = when (turn) {
                    TURN_RIGHT -> MapDirection.UP
                    else -> MapDirection.DOWN
                }
            }

            MapDirection.UP -> {
                newDirection = when (turn) {
                    TURN_RIGHT -> MapDirection.RIGHT
                    else -> MapDirection.LEFT
                }
            }
        }
    }
    return newDirection
}

enum class MapDirection(val directionValue: Int) {
    RIGHT (0),
    DOWN (1),
    LEFT (2),
    UP (3)
}

private class Point22(var x: Int, var y: Int, var direction: MapDirection) {
    override fun toString(): String {
        return "($x, $y, $direction)"
    }
}
