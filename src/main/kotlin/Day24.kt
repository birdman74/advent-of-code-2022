//private const val FILENAME: String = "day24-sample.txt"
//private const val FILENAME: String = "day24-sample2.txt"
private const val FILENAME: String = "day24.txt"

private const val NORTH = '^'
private const val SOUTH = 'v'
private const val WEST = '<'
private const val EAST = '>'

private const val lastNorthY = 1
private const val lastWestX = 1
private var lastSouthY = 0
private var lastEastX = 0

private val northStorms: MutableSet<Position> = mutableSetOf()
private val southStorms: MutableSet<Position> = mutableSetOf()
private val westStorms: MutableSet<Position> = mutableSetOf()
private val eastStorms: MutableSet<Position> = mutableSetOf()

private val startPosition = Position(1, 0)
private var targetPositions: MutableList<Position> = mutableListOf()

fun main() {
    val input = Common.readFile(FILENAME)
    val initialPosition = Position(1, 0)
    var partyPositions: Set<Position> = mutableSetOf(initialPosition)

    parseMap(input)

    val exitX = input[0].length - 2
    val exitY = input.size - 1

    targetPositions.add(Position(exitX, exitY))
    targetPositions.add(initialPosition)
    targetPositions.add(Position(exitX, exitY))

    val stormPositions: MutableList<MutableSet<Position>> = mutableListOf()
    for (i in 1..(lastSouthY * lastEastX)) {
        moveStorms()
        val newCoordinates = stormCoordinates()
        if (!stormPositions.contains(newCoordinates)) {
            stormPositions.add(stormCoordinates())
        } else {
            break
        }
    }

    var reachedFinalDestination = false
    var moves = 0
    var targetPositionIndex = 0

    while(!reachedFinalDestination) {
        var newPartyPositions: Set<Position> = setOf()

        for (partyPosition in partyPositions) {
            newPartyPositions = newPartyPositions.union(partyPosition.possibleMoves())
        }
        partyPositions = newPartyPositions.subtract(stormPositions[moves % stormPositions.size])

        moves++

        if (partyPositions.contains(targetPositions[targetPositionIndex])) {
            println("Found location #${targetPositionIndex}. Number of moves: ${moves}\n")
            partyPositions = mutableSetOf(targetPositions[targetPositionIndex])
            targetPositionIndex++
            if (targetPositionIndex == targetPositions.size) {
                reachedFinalDestination = true
            }
        }
    }
}

private fun parseMap(map: List<String>) {
    lastSouthY = map.size - 2
    lastEastX = map[0].length - 2

    for ((y, line) in map.withIndex()) {
        for ((x, spot) in line.withIndex()) {
            when(spot) {
                NORTH -> northStorms.add(Position(x, y))
                SOUTH -> southStorms.add(Position(x, y))
                WEST -> westStorms.add(Position(x, y))
                EAST -> eastStorms.add(Position(x, y))
                else -> continue
            }
        }
    }
}

private fun moveStorms() {
    northStorms.forEach { s -> s.y = if (s.y == lastNorthY) { lastSouthY } else { s.y - 1 } }
    southStorms.forEach { s -> s.y = if (s.y == lastSouthY) { lastNorthY } else { s.y + 1 } }
    westStorms.forEach { s -> s.x = if (s.x == lastWestX) { lastEastX } else { s.x - 1 } }
    eastStorms.forEach { s -> s.x = if (s.x == lastEastX) { lastWestX } else { s.x + 1 } }
}

private fun stormCoordinates(): MutableSet<Position> {
    val coordinateSet = northStorms.union(southStorms).union(westStorms).union(eastStorms)
    val setClone: MutableSet<Position> = mutableSetOf()
    for (pos in coordinateSet) {
        setClone.add(Position(pos.x, pos.y))
    }
    return setClone
}

private open class Position(var x: Int, var y: Int) {
    fun possibleMoves(): Set<Position> {
        val possibleMoves: MutableSet<Position> = mutableSetOf()
        possibleMoves.add(this)
        var newPos = Position(x - 1, y)
        if (newPos.onTheMap()) {
            possibleMoves.add(newPos)
        }
        newPos = Position(x + 1, y)
        if (newPos.onTheMap()) {
            possibleMoves.add(newPos)
        }
        newPos = Position(x, y - 1)
        if (newPos.onTheMap()) {
            possibleMoves.add(newPos)
        }
        newPos = Position(x, y + 1)
        if (newPos.onTheMap()) {
            possibleMoves.add(newPos)
        }

        return possibleMoves.toSet()
    }

    fun onTheMap(): Boolean {
        return this == startPosition ||
                this == targetPositions[0] ||
                (y in lastNorthY..lastSouthY &&
                        x in lastWestX..lastEastX)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Position) return false

        return (x == other.x) && (y == other.y)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}
