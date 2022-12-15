//private const val FILENAME: String = "day12-sample.txt"
private const val FILENAME: String = "day12.txt"

private var topMap: MutableList<String> = Common.readFile(FILENAME).toMutableList()
private var minPathMap: MutableList<MutableList<Int>> = MutableList(topMap.size) { MutableList(topMap[0].length) { Int.MAX_VALUE } }

fun main() {
//    part01()
    part02()
}

private fun part01() {
    println("Part 1:\n")

    val destination: Point12 = walkTheMap('S', 'a', 'E', 'z', ::scaleUpNoMoreThanOne)

    println("Number of steps to reach destination: ${ minPathMap[destination.x][destination.y] }\n")
}

private fun scaleUpNoMoreThanOne(fromPoint12: Point12, toPoint12: Point12) : Boolean {
    return topMap[toPoint12.x][toPoint12.y].code <= (topMap[fromPoint12.x][fromPoint12.y].code + 1)
}

private fun part02() {
    println("Part 2:\n")

    val destination: Point12 = walkTheMap('E', 'z', 'S', 'a', ::scaleDownNoMoreThanOne)


    val fastestRoute: Int = topMap.mapIndexed { x, row ->
        row.mapIndexed { y, col ->
            if (topMap[x][y] != 'a' || minPathMap[x][y] == 0) {
                Int.MAX_VALUE
            } else {
                minPathMap[x][y]
            }
        }.min()
    }.min()

    println("Fastest route down: $fastestRoute\n")
}

private fun scaleDownNoMoreThanOne(fromPoint12: Point12, toPoint12: Point12) : Boolean {
    return topMap[toPoint12.x][toPoint12.y].code >= (topMap[fromPoint12.x][fromPoint12.y].code - 1)
}

private fun walkTheMap(startMarker: Char, startElevation: Char,
                       endMarker: Char, endElevation: Char,
                       canMove: (fromPoint12: Point12, toPoint12: Point12) -> Boolean) : Point12 {

    var origin: Point12 = Point12(-1, -1, 0)
    var destination: Point12 = Point12(-1, -1, 0)

    topMap.forEachIndexed { index, s ->
        if (s.contains(startMarker)) {
            origin = Point12(index, s.indexOf(startMarker), 0)
            minPathMap[origin.x][origin.y] = 0
            topMap[index] = topMap[index].replace(startMarker, startElevation)
        }
        if (s.contains(endMarker)) {
            destination = Point12(index, s.indexOf(endMarker), 0)
            topMap[index] = topMap[index].replace(endMarker, endElevation)
        }
    }

    origin.crawlTo(destination, canMove)

    return destination
}

private class Point12(val x: Int, val y: Int, private val stepsToPoint: Int) {

    fun crawlTo(destination: Point12, canMove: (fromPoint12: Point12, toPoint12: Point12) -> Boolean) {
        val possibleSteps = listOf<Point12>(left(), above(), right(), below())
        val currentPointElev: Int = topMap[x][y].code
        for (point in possibleSteps) {
            if (point.x in 0 until topMap.size && point.y in 0 until topMap[0].length &&
                minPathMap[point.x][point.y] > point.stepsToPoint &&
                canMove.invoke(this, point)
            ) {
                minPathMap[point.x][point.y] = point.stepsToPoint
                if (point != destination) {
                    point.crawlTo(destination, canMove)
                }
            }
        }

    }

    private fun above(): Point12 {
        return Point12(x - 1, y, stepsToPoint + 1)
    }

    private fun below(): Point12 {
        return Point12(x + 1, y, stepsToPoint + 1)
    }

    private fun left(): Point12 {
        return Point12(x, y - 1, stepsToPoint + 1)
    }

    private fun right(): Point12 {
        return Point12(x, y + 1, stepsToPoint + 1)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Point12) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }
}