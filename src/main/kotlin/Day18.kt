import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

//private const val FILENAME: String = "day18-sample.txt"
private const val FILENAME: String = "day18.txt"
fun main() {
    println("Part 1:\n")
    val input = Common.readFile(FILENAME)
    var minX: Int = Int.MAX_VALUE
    var maxX: Int = Int.MIN_VALUE
    var minY: Int = Int.MAX_VALUE
    var maxY: Int = Int.MIN_VALUE
    var minZ: Int = Int.MAX_VALUE
    var maxZ: Int = Int.MIN_VALUE

    val droplets: MutableList<Droplet> = mutableListOf<Droplet>()
    input.forEach {
        val coordinates = it.split(",")
        val x = coordinates[0].toInt()
        minX = min(minX, x)
        maxX = max(maxX, x)
        val y = coordinates[1].toInt()
        minY = min(minY, y)
        maxY = max(maxY, y)
        val z = coordinates[2].toInt()
        minZ = min(minZ, z)
        maxZ = max(maxZ, z)
        droplets.add(Droplet(x, y, z))
    }

    println("Total surface area: ${ exposedSides(droplets) }\n") //4282 (correct)

    println("Part 2:\n")  // 1968 (low), 2348 (low)

    val possibleVoidsPoints: MutableList<Droplet> = mutableListOf<Droplet>()

    for (x in minX..maxX) {
        for (y in minY..maxY) {
            var dropletsStart: Boolean = false
            var possibleZVoids: MutableList<Droplet> = mutableListOf<Droplet>()
            for (z in minZ..maxZ) {
                val point = Droplet(x, y, z)
                if (dropletsStart) {
                    if (!droplets.contains(point)) {
                        possibleZVoids.add(point)
                    } else if (possibleZVoids.size > 0) {
                        possibleVoidsPoints.addAll(possibleZVoids)
                        possibleZVoids = mutableListOf<Droplet>()
                    }
                } else if (droplets.contains(point)) {
                    dropletsStart = true
                }
            }
        }
    }
    
    val possibleVoidPockets: MutableList<MutableList<Droplet>> = mutableListOf<MutableList<Droplet>>()
    for (possibleVoidPoint in possibleVoidsPoints) {
        val touchingVoidPockets: MutableList<MutableList<Droplet>> = mutableListOf<MutableList<Droplet>>()
        for (possibleVoidPocket in possibleVoidPockets) {
            for (point in possibleVoidPocket) {
                if (!touchingVoidPockets.contains(possibleVoidPocket) && point.touching(possibleVoidPoint)) {
                    touchingVoidPockets.add(possibleVoidPocket)
                }
            }
        }
        when (touchingVoidPockets.size) {
            0 -> {
                val newPocket: MutableList<Droplet> = mutableListOf<Droplet>()
                newPocket.add(possibleVoidPoint)
                possibleVoidPockets.add(newPocket)
            }
            1 -> { touchingVoidPockets[0].add(possibleVoidPoint) }
            else -> {
                for (i in 1 until touchingVoidPockets.size) {
                    touchingVoidPockets[0].addAll(touchingVoidPockets[i])
                    possibleVoidPockets.remove(touchingVoidPockets[i])
                }
                touchingVoidPockets[0].add(possibleVoidPoint)
            }
        }
    }

    val voidPockets: MutableList<MutableList<Droplet>> = mutableListOf<MutableList<Droplet>>()

    for (possibleVoidPocket in possibleVoidPockets) {
        if (!voidHasExternalExposure(possibleVoidPocket, droplets, minX, maxX, minY, maxY)) {
            voidPockets.add(possibleVoidPocket)
        }
    }

    val voidSides = voidPockets.sumOf { exposedSides(it) }

    println("Total surface area: ${ exposedSides(droplets) - voidSides }\n")
}

private fun voidHasExternalExposure(void: MutableList<Droplet>,
                                    droplets: MutableList<Droplet>,
                                    minX: Int, maxX: Int,
                                    minY: Int, maxY: Int): Boolean {

    for (point in void) {
        if (hasExposure(point, droplets, 'X', false, minX) ||
            hasExposure(point, droplets, 'X', true, maxX) ||
            hasExposure(point, droplets, 'Y', false, minY) ||
            hasExposure(point, droplets, 'Y', true, maxY)) {
            return true
        }
    }
    return false
}

private fun hasExposure(point: Droplet, droplets: MutableList<Droplet>, axis: Char, above: Boolean, limit: Int): Boolean {
    when (axis) {
        'X' -> {
            when (above) {
                true -> {
                    for (i in point.x + 1..limit) {
                        if (droplets.contains(Droplet(i, point.y, point.z))) {
                            return false
                        }
                    }
                }
                false -> {
                    for (i in limit until point.x) {
                        if (droplets.contains(Droplet(i, point.y, point.z))) {
                            return false
                        }
                    }
                }
            }
        }
        'Y' -> {
            when (above) {
                true -> {
                    for (i in point.y + 1..limit) {
                        if (droplets.contains(Droplet(point.x, i, point.z))) {
                            return false
                        }
                    }
                }
                false -> {
                    for (i in limit until point.y) {
                        if (droplets.contains(Droplet(point.x, i, point.z))) {
                            return false
                        }
                    }
                }
            }
        }
    }
    return true
}

private fun exposedSides(droplets: List<Droplet>) : Int {
    var exposedSides: Int = 6 * droplets.size
    for (i in droplets.indices) {
        for (j in i + 1 until droplets.size) {
            if (droplets[i].touching(droplets[j])) {
                exposedSides -= 2
            }
        }
    }
    return exposedSides
}

private class Droplet(val x: Int, val y: Int, val z: Int) {
    fun touching(other: Droplet): Boolean {
        return ((x == other.x && y == other.y && (abs(z - other.z) == 1)) ||
                ((abs(x - other.x) == 1) && y == other.y && z == other.z) ||
                (x == other.x && (abs(y - other.y) == 1) && z == other.z))
    }

    override fun equals(other: Any?): Boolean {
        return other is Droplet &&
                x == other.x && y == other.y && z == other.z
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }
}