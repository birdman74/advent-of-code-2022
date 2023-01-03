import java.util.NoSuchElementException
import kotlin.math.max

//private const val FILENAME: String = "day19-sample.txt"   // 56, 62, X
private const val FILENAME: String = "day19.txt"        // 4712 (low), 5301 (correct)

// 31 * 8 * 19 = 4712
// 31 * 9 * 19 = 5301

private const val TOTAL_CYCLES: Int = 32 // Part 1: 24, Part 2: 32
private var minimumGeodeRobots: MutableList<Int> = mutableListOf<Int>()
private var currentGeodeMax: Int = 0

private var maxOreBots: Int = 0
private var maxClayBots: Int = 0
private var maxObsidianBots: Int = 0

private val robotPriorityOrder: List<ResourceType> = listOf(
    ResourceType.GEODE,
    ResourceType.OBSIDIAN,
    ResourceType.CLAY,
    ResourceType.ORE)

fun main() {
    println("Part 2:\n")

    val input = Common.readFile(FILENAME)
    var geodeProduct: Int = 1

    for (i in 0..2) {
        minimumGeodeRobots = mutableListOf<Int>(0, 0)
        currentGeodeMax = 0
        val staff: MutableList<ResourceType> = mutableListOf(ResourceType.ORE)
        val inventory: MutableMap<ResourceType, Int> = mutableMapOf(
            ResourceType.ORE to 0,
            ResourceType.CLAY to 0,
            ResourceType.OBSIDIAN to 0,
            ResourceType.GEODE to 0
        )

        val costs: Map<ResourceType, Map<ResourceType, Int>> = parseBlueprint(input[i])

        val numGeodes = maxGeodesHarvested(TOTAL_CYCLES, staff, inventory, costs)

        println("Number of geodes for blueprint ${ i + 1 }: $numGeodes")

        geodeProduct *= numGeodes
    }

    println("Geode product is $geodeProduct\n")
}

/*
    Given: Robot Factory + 1 ore-collecting robot
    Time Span: 24 minutes

    * Robots collect resources: 1 / minute
    * Robot Factory creates: 1 robot / minute + resources (at beginning of minute)
    * Ore -> Ore Robots OR Clay Robots
    * Ore + Clay -> Obsidian Robots
    * Ore + Obsidian -> Geode Robots
*/

private fun maxGeodesHarvested(cyclesLeft: Int,
                               staff: MutableList<ResourceType>,
                               inventory: MutableMap<ResourceType, Int>,
                               costs: Map<ResourceType, Map<ResourceType, Int>>): Int {

    val numGeodeRobots = staff.count { it == ResourceType.GEODE }
    if (cyclesLeft > minimumGeodeRobots[0] && numGeodeRobots > minimumGeodeRobots[1]) {
        minimumGeodeRobots = mutableListOf(cyclesLeft, numGeodeRobots)
    } else if (cyclesLeft <= minimumGeodeRobots[0] && numGeodeRobots < minimumGeodeRobots[1]) {
        return 0
    }

    val maxGeodesForThread: Int =
        calculateMaxGeodesPossible(inventory[ResourceType.GEODE]!!, staff.count { it == ResourceType.GEODE }, cyclesLeft)

    if (maxGeodesForThread < currentGeodeMax) {
        return 0
    }

    val inventoryWithoutManufacturing = harvest(staff, inventory)
    val staffWithoutManufacturing = staff.toMutableList()

    if (cyclesLeft == 1) {
        setMaxGeodes(inventoryWithoutManufacturing[ResourceType.GEODE]!!)
        return inventoryWithoutManufacturing[ResourceType.GEODE]!!
    }

    // spend resources
    var robotsCanAfford: List<ResourceType> = mutableListOf<ResourceType>()
    try {
        robotsCanAfford = robotPriorityOrder.filter { robotType -> canAfford(costs[robotType]!!, inventory) }
    } catch (_: NoSuchElementException) {}

    if (staff.count { it == ResourceType.ORE } >= maxOreBots) {
        robotsCanAfford = robotsCanAfford.filterNot { it == ResourceType.ORE }
    }
    if (staff.count { it == ResourceType.CLAY } >= maxClayBots) {
        robotsCanAfford = robotsCanAfford.filterNot { it == ResourceType.CLAY }
    }
    if (staff.count { it == ResourceType.OBSIDIAN } >= maxObsidianBots) {
        robotsCanAfford = robotsCanAfford.filterNot { it == ResourceType.OBSIDIAN }
    }

    val maxAfterManufacturing = robotsCanAfford.maxOfOrNull { robotType ->
        maxGeodesAfterManufacturing(cyclesLeft - 1, robotType, staff, inventory, costs)
    }

    val maxWithoutManufacturing = maxGeodesHarvested(cyclesLeft - 1,
        staffWithoutManufacturing,
        inventoryWithoutManufacturing,
        costs)

    if (maxAfterManufacturing != null) {
        val retValue = max(maxAfterManufacturing, maxWithoutManufacturing)
        setMaxGeodes(retValue)
        return retValue
    }

    setMaxGeodes(maxWithoutManufacturing)
    return maxWithoutManufacturing
}

fun calculateMaxGeodesPossible(geodes: Int, geodeBots: Int, cyclesLeft: Int): Int {
    var maxGeodes = geodes
    for (i in 1..cyclesLeft) {
        maxGeodes += (geodeBots + i - 1)
    }
    return maxGeodes
}

fun setMaxGeodes(newCount: Int) {
    if (newCount > currentGeodeMax) {
        currentGeodeMax = newCount
    }
}

private fun maxGeodesAfterManufacturing(cyclesLeft: Int,
                                        robotType: ResourceType,
                                        staff: MutableList<ResourceType>,
                                        inventory: MutableMap<ResourceType, Int>,
                                        costs: Map<ResourceType, Map<ResourceType, Int>>): Int {
    // harvest
    val newInventory: MutableMap<ResourceType, Int> = harvest(staff, inventory)

    costs[robotType]?.forEach { cost ->
        newInventory[cost.key] = newInventory[cost.key]!! - cost.value
    }

    val newStaff = staff.toMutableList()
    newStaff.add(robotType)
//    println("Created a $robotType robot in minute ${ TOTAL_CYCLES - cyclesLeft }. Staff: $newStaff")

    return maxGeodesHarvested(cyclesLeft, newStaff, newInventory, costs)
}

private fun harvest(staff: MutableList<ResourceType>,
                    inventory: MutableMap<ResourceType, Int>): MutableMap<ResourceType, Int> {
    val newInventory = inventory.toMutableMap()
    for (robotType in staff) {
        newInventory[robotType] = newInventory[robotType]!! + 1
    }
    return newInventory
}

private fun canAfford(costs: Map<ResourceType, Int>, inventory: MutableMap<ResourceType, Int>): Boolean {
    for (cost in costs) {
        if (cost.value > inventory[cost.key]!!) {
            return false
        }
    }
    return true
}

private fun parseBlueprint(input: String): Map<ResourceType, Map<ResourceType, Int>> {
    val costs: MutableMap<ResourceType, Map<ResourceType, Int>> = mutableMapOf<ResourceType, Map<ResourceType, Int>>()

    val pieces = input.split(" ")

    costs[ResourceType.ORE] = mapOf(ResourceType.ORE to pieces[6].toInt())
    costs[ResourceType.CLAY] = mapOf(ResourceType.ORE to pieces[12].toInt())
    costs[ResourceType.OBSIDIAN] = mapOf(ResourceType.ORE to pieces[18].toInt(),
                                         ResourceType.CLAY to pieces[21].toInt())
    costs[ResourceType.GEODE] = mapOf(ResourceType.ORE to pieces[27].toInt(),
                                      ResourceType.OBSIDIAN to pieces[30].toInt())

    maxOreBots = listOf(costs[ResourceType.ORE]!![ResourceType.ORE]!!,
                        costs[ResourceType.CLAY]!![ResourceType.ORE]!!,
                        costs[ResourceType.OBSIDIAN]!![ResourceType.ORE]!!,
                        costs[ResourceType.GEODE]!![ResourceType.ORE]!!).max()
    maxClayBots = costs[ResourceType.OBSIDIAN]!![ResourceType.CLAY]!!
    maxObsidianBots = costs[ResourceType.GEODE]!![ResourceType.OBSIDIAN]!!

    return costs.toMap()
}
