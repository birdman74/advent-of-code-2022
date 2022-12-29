import java.lang.Integer.min
import java.lang.Integer.max

//private const val FILENAME: String = "day14-sample.txt"
private const val FILENAME: String = "day14.txt"

private const val ORIGIN_X: Int = 500
private const val ORIGIN_Y: Int = 0

private const val ROCK: Char = '#'
private const val SAND: Char = 'o'
private const val SPACE: Char = '.'

fun main() {
    val originalMap: MutableMap<Int, MutableMap<Int, Char>> = createMap()
    var part2Map: MutableMap<Int, MutableMap<Int, Char>> = copyMap(originalMap)

    println("Part 1:\n")
    var grains: Int = 0
    while (dropSand(originalMap, 500, 0, mutableMapOf<Int, Char>())) {
        grains += 1
    }
    println("$grains grains of sand dropped before abyss\n")

    println("Part 2:\n")
    val mapFloor = findFloorToMap(part2Map)
    part2Map.forEach { (_, t) -> t[mapFloor] = ROCK }
    grains = 0
    while (part2Map[ORIGIN_X]?.get(ORIGIN_Y) != SAND) {
        dropSand(part2Map, 500, 0, getFloorColumn(mapFloor))
        grains += 1
    }
    println("$grains grains of sand dropped before blockage\n")
}

private fun dropSand(map: MutableMap<Int, MutableMap<Int, Char>>, prevX: Int, prevY: Int,
                     defaultColumn: MutableMap<Int, Char>): Boolean {
    var x: Int = prevX
    var y: Int = prevY
    // move down
    var currentColumn: MutableMap<Int, Char> = map.getOrDefault(x, defaultColumn.toMutableMap())
    if (currentColumn.isEmpty()) {
        return false
    } else {
        val filledSpacesBelow = currentColumn.keys.filter { it > y }
        if (filledSpacesBelow.isEmpty()) {
            return false
        } else {
            y = filledSpacesBelow.min() - 1
            map[x] = currentColumn
        }
    }
    // move down-left diagonally
    var colToLeft: MutableMap<Int, Char> = map.getOrDefault(x - 1, defaultColumn.toMutableMap())
    if (colToLeft.isEmpty()) {
        return false
    } else {
        var destination = colToLeft.getOrDefault(y + 1, SPACE)
        if (destination == SPACE) {
            x -= 1
            y += 1
        } else {        // move down-right diagonally
            var colToRight: MutableMap<Int, Char> = map.getOrDefault(x + 1, defaultColumn.toMutableMap())
            if (colToRight.isEmpty()) {
                return false
            } else {
                destination = colToRight.getOrDefault(y + 1, SPACE)
                if (destination == SPACE) {
                    x += 1
                    y += 1
                } else {
                    currentColumn[y] = SAND
                    return true
                }
            }
        }
    }

    return dropSand(map, x, y, defaultColumn)
}

private fun getFloorColumn(floor: Int): MutableMap<Int, Char> {
    var floorColumn: MutableMap<Int, Char> = mutableMapOf<Int, Char>()
    floorColumn[floor] = ROCK

    return floorColumn
}

private fun copyMap(map: MutableMap<Int, MutableMap<Int, Char>>): MutableMap<Int, MutableMap<Int, Char>> {
    var newMap: MutableMap<Int, MutableMap<Int, Char>> = mutableMapOf<Int, MutableMap<Int, Char>>()

    map.forEach { (x, yColumn) ->
        newMap[x] = yColumn.toMutableMap()
    }

    return newMap
}

private fun createMap(): MutableMap<Int, MutableMap<Int, Char>> {
    val input = Common.readFile(FILENAME)
    val map: MutableMap<Int, MutableMap<Int, Char>> = mutableMapOf<Int, MutableMap<Int, Char>>()

    input.forEach { path ->
        val pathPoints: List<List<Int>> = path.split(" -> ").map {
                point -> point.split(",").map(String::toInt)
        }
        for (i: Int in 0..pathPoints.size - 2) {
            val x1 = pathPoints[i][0]
            val y1 = pathPoints[i][1]
            val x2 = pathPoints[i + 1][0]
            val y2 = pathPoints[i + 1][1]

            if (x1 == x2) { // vertical line
                val yRocks = map.getOrDefault(x1, mutableMapOf<Int, Char>())
                val lowY = min(y1, y2)
                val highY = max(y1, y2)
                for (y: Int in lowY..highY) {
                    yRocks[y] = ROCK
                }
                map[x1] = yRocks
            } else {        // horizontal line
                val lowX = min(x1, x2)
                val highX = max(x1, x2)
                for (x: Int in lowX .. highX) {
                    val yRocks = map.getOrDefault(x, mutableMapOf<Int, Char>())
                    yRocks[y1] = ROCK
                    map[x] = yRocks
                }
            }
        }
    }
    return map
}

private fun findFloorToMap(map: MutableMap<Int, MutableMap<Int, Char>>): Int {
    return map.values.map {
        it.keys.max()
    }.max() + 2
}
