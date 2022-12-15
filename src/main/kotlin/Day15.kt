import java.lang.Long.max
import java.lang.Long.min
import kotlin.math.abs
import kotlin.system.exitProcess

//private const val FILENAME: String = "day15-sample.txt"
private const val FILENAME: String = "day15.txt"

//private const val Y_TARGET: Long = 10L
private const val Y_TARGET: Long = 2000000L

//private const val SPAN: Long = 20L
private const val SPAN: Long = 4000000L

private val input = Common.readFile(FILENAME)

fun main() {
    part01(yTarget = Y_TARGET, part1Only = true, includeBeaconLocations = false)
    part02()
}

private fun part01(yTarget: Long, part1Only: Boolean, includeBeaconLocations: Boolean): MutableList<ClosedRange<Long>> {
    if (part1Only) {
        println("Part 1:\n")
    }

    val coverages: MutableList<ClosedRange<Long>> = mutableListOf<ClosedRange<Long>>()
    val beaconXCoordsAtY: MutableMap<Long, MutableList<Long>> = HashMap<Long, MutableList<Long>>()

    input.forEach { line ->
        val pieces = line.split(' ')
        val sensorLocation = Point15(parseCoordinate(pieces[2]), parseCoordinate(pieces[3]))
        val beaconLocation = Point15(parseCoordinate(pieces[8]), parseCoordinate(pieces[9]))
        //println ("$sensorLocation -> $beaconLocation = ${ distance(sensorLocation, beaconLocation) }")
        var beaconsAtY: MutableList<Long> = mutableListOf<Long>()
        if (beaconXCoordsAtY.keys.contains(beaconLocation.y)) {
            beaconsAtY = beaconXCoordsAtY[beaconLocation.y]!!
            if (!beaconsAtY.contains(beaconLocation.x)) {
                beaconsAtY.add(beaconLocation.x)
            }
        } else {
            beaconsAtY.add(beaconLocation.x)
            beaconXCoordsAtY[beaconLocation.y] = beaconsAtY
        }

        val oneWayCoverageRange: Long = sensorLocation.coverageDistanceInOneDirection(
            distance(sensorLocation, beaconLocation), yTarget
        )
        if (oneWayCoverageRange > -1) {
            coverages.add((sensorLocation.x - oneWayCoverageRange)..(sensorLocation.x + oneWayCoverageRange))
        }
    }

    var continueMerging = true
    while (continueMerging) {
        continueMerging = false
        var mergedCoverages: ClosedRange<Long> = coverages.removeLast()
        for (i in (coverages.size - 1).downTo(0)) {
            val mergeResult: List<ClosedRange<Long>> = rangeUnion(mergedCoverages, coverages[i])
            if (mergeResult.size == 1) {
                continueMerging = true
                coverages.removeAt(i)
                mergedCoverages = mergeResult[0]
            }
        }
        coverages.add(0, mergedCoverages)
    }

    var nonBeaconPoints: Long = coverages.sumOf { it.endInclusive - it.start + 1 }
    if (!includeBeaconLocations) {
        beaconXCoordsAtY[yTarget]?.forEach {
            for (coverage in coverages) {
                if (coverage.contains(it)) {
                    nonBeaconPoints -= 1
                }
            }
        }
    }

    if (part1Only) {
        println("Total positions that cannot contain a beacon: $nonBeaconPoints\n")
    }

    return coverages
}

private fun parseCoordinate(str: String): Long {
    return str.split('=')[1].removeSuffix(",").removeSuffix(":").toLong()
}

private fun part02() {
    println("Part 2:\n")
    var coverages: List<ClosedRange<Long>>

    for (i in 0L..SPAN) {
        coverages = part01(yTarget = i, part1Only = false, includeBeaconLocations = true).filter { coverage ->
            coverage.contains(0) || coverage.contains(SPAN)
        }
        var x: Long = -1
        if (coverages.size == 2) {
            x = if (coverages[0].start <= 0) {
                coverages[0].endInclusive + 1
            } else {
                coverages[0].start - 1
            }
        } else if (coverages.size == 1) {
            if (!coverages[0].contains(0)) {
                x = 0
            } else if (!coverages[0].contains(SPAN)) {
                x = SPAN
            }
        }
        if (x >= 0) {
            println("Missing Beacon Frequency: ${ (x * 4000000) + i }\n")
            exitProcess(0)
        }
    }
}

private fun distance(s: Point15, b: Point15): Long {
    return abs(s.x - b.x) + abs(s.y - b.y)
}

private class Point15(val x: Long, val y: Long) {

    override fun toString(): String {
        return "($x, $y)"
    }

    fun coverageDistanceInOneDirection(maxRange: Long, yTarget: Long): Long {
        return maxRange - abs(y - yTarget)
    }
}

private fun rangeUnion(first: ClosedRange<Long>, second: ClosedRange<Long>): List<ClosedRange<Long>> {
    return if (first.contains(second.start) || first.contains(second.endInclusive) ||
        second.contains(first.start) || second.contains(first.endInclusive) ||
        (first.start == second.endInclusive + 1) || (first.endInclusive == second.start - 1)) {
        listOf(min(first.start, second.start)..max(first.endInclusive, second.endInclusive))
    } else {
        listOf(first, second)
    }
}