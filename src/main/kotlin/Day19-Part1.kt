import java.util.NoSuchElementException
import kotlin.math.max

private const val FILENAME: String = "day19-sample.txt"   // 33 (correct: 9 + 24)
//private const val FILENAME: String = "day19.txt"        // 1650 (low), 1663 (low), 1703 (correct)

// 2 0 0 28 25 36 0 72 36 0 132 0 130 0 60 0 17 54 57 0 63 66 46 0 25 0 0 364 0 450 = 1663
// 3 0 3 36 25 36 0 72 45 0 132 0 130 0 60 0 17 54 76 0 63 66 46 0 25 0 0 364 0 450 = 1703

private const val TOTAL_CYCLES: Int = 24
private var minimumGeodeRobots: MutableList<Int> = mutableListOf<Int>()

private val robotPriorityOrder: List<ResourceType> = listOf(
    ResourceType.GEODE,
    ResourceType.OBSIDIAN,
    ResourceType.CLAY,
    ResourceType.ORE)

fun main() {
    println("Part 1:\n")

    val input = Common.readFile(FILENAME)
    var totalQualityLevel: Int = 0

    input.forEachIndexed() { index, line ->
        minimumGeodeRobots = mutableListOf<Int>(0, 0)
        val staff: MutableList<ResourceType> = mutableListOf(ResourceType.ORE)
        val inventory: MutableMap<ResourceType, Int> = mutableMapOf(
            ResourceType.ORE to 0,
            ResourceType.CLAY to 0,
            ResourceType.OBSIDIAN to 0,
            ResourceType.GEODE to 0
        )

        val costs: Map<ResourceType, Map<ResourceType, Int>> = parseBlueprint(line)

        val qualityLevel = ((index + 1) * maxGeodesHarvested(TOTAL_CYCLES, staff, inventory, costs))

        println("Quality level for blueprint ${ index + 1 }: $qualityLevel")

        totalQualityLevel += qualityLevel
    }

    println("Total quality level of ${ input.size } blueprints: $totalQualityLevel\n")
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

    val inventoryWithoutManufacturing = harvest(staff, inventory)
    val staffWithoutManufacturing = staff.toMutableList()

    if (cyclesLeft == 1) {
        return inventoryWithoutManufacturing[ResourceType.GEODE]!!
    }

    // spend resources
    var robotsCanAfford: List<ResourceType> = mutableListOf<ResourceType>()
    try {
        robotsCanAfford = robotPriorityOrder.filter { robotType -> canAfford(costs[robotType]!!, inventory) }
    } catch (_: NoSuchElementException) {}

    if (cyclesLeft < TOTAL_CYCLES / 3) {
        robotsCanAfford = robotsCanAfford.filter { it in listOf(ResourceType.GEODE, ResourceType.OBSIDIAN) }
    }

    val maxAfterManufacturing = robotsCanAfford.maxOfOrNull { robotType ->
        maxGeodesAfterManufacturing(cyclesLeft - 1, robotType, staff, inventory, costs)
    }

    val maxWithoutManufacturing = maxGeodesHarvested(cyclesLeft - 1,
        staffWithoutManufacturing,
        inventoryWithoutManufacturing,
        costs)

    if (maxAfterManufacturing != null) {
        return max(maxAfterManufacturing, maxWithoutManufacturing)
    }

    return maxWithoutManufacturing
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

    return costs.toMap()
}

enum class ResourceType {
    ORE, CLAY, OBSIDIAN, GEODE
}