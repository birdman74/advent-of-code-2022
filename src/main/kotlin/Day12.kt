import javax.swing.text.StyledEditorKit.BoldAction

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

    val destination: Point = walkTheMap('S', 'a', 'E', 'z', ::scaleUpNoMoreThanOne)

    println("Number of steps to reach destination: ${ minPathMap[destination.x][destination.y] }\n")
}

fun scaleUpNoMoreThanOne(fromPoint: Point, toPoint: Point) : Boolean {
    return topMap[toPoint.x][toPoint.y].code <= (topMap[fromPoint.x][fromPoint.y].code + 1)
}

private fun part02() {
    println("Part 2:\n")

    val destination: Point = walkTheMap('E', 'z', 'S', 'a', ::scaleDownNoMoreThanOne)


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

fun scaleDownNoMoreThanOne(fromPoint: Point, toPoint: Point) : Boolean {
    return topMap[toPoint.x][toPoint.y].code >= (topMap[fromPoint.x][fromPoint.y].code - 1)
}

private fun walkTheMap(startMarker: Char, startElevation: Char,
                       endMarker: Char, endElevation: Char,
                       canMove: (fromPoint: Point, toPoint: Point) -> Boolean) : Point {

    var origin: Point = Point(-1, -1, 0)
    var destination: Point = Point(-1, -1, 0)

    topMap.forEachIndexed { index, s ->
        if (s.contains(startMarker)) {
            origin = Point(index, s.indexOf(startMarker), 0)
            minPathMap[origin.x][origin.y] = 0
            topMap[index] = topMap[index].replace(startMarker, startElevation)
        }
        if (s.contains(endMarker)) {
            destination = Point(index, s.indexOf(endMarker), 0)
            topMap[index] = topMap[index].replace(endMarker, endElevation)
        }
    }

    origin.crawlTo(destination, canMove)

    return destination
}

class Point (val x: Int, val y: Int, private val stepsToPoint: Int) {

    fun crawlTo(destination: Point, canMove: (fromPoint: Point, toPoint: Point) -> Boolean) {
        val possibleSteps = listOf<Point>(left(), above(), right(), below())
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

    private fun above(): Point {
        return Point(x - 1, y, stepsToPoint + 1)
    }

    private fun below(): Point {
        return Point(x + 1, y, stepsToPoint + 1)
    }

    private fun left(): Point {
        return Point(x, y - 1, stepsToPoint + 1)
    }

    private fun right(): Point {
        return Point(x, y + 1, stepsToPoint + 1)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Point) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }
}