//private const val FILENAME: String = "day16-sample.txt"
private const val FILENAME: String = "day16.txt"

private var valves: MutableMap<String, Valve01> = HashMap<String, Valve01>()

fun main() {
    initValves()

    val openValves: MutableList<String> = mutableListOf<String>()

    valves.values.forEach {
        if (it.rate == 0) {
            openValves.add(it.name)
        }
        it.calculateHops()
    }

    println("Max pressure released in 30 minutes: ${ valves["AA"]?.maxPressureIn(30, 0, openValves)}\n")
}

private fun initValves() {
    val input = Common.readFile(FILENAME)

    valves = HashMap<String, Valve01>()

    input.forEach { line ->
        val pieces = line.split(" ")
        val name: String = pieces[1]
        val rate: Int = pieces[4].split("=")[1].split(";")[0].toInt()
        val nextValveNames = pieces.subList(9, pieces.size).map { it.substring(0, 2) }

        val valve01: Valve01
        if (valves.keys.contains(name)) {
            valve01 = valves[name]!!
        } else {
            valve01 = Valve01(name)
            valves[name] = valve01
        }
        valve01.rate = rate

        for (nextValveName in nextValveNames) {
            var nextValve01: Valve01
            if (valves.keys.contains(nextValveName)) {
                nextValve01 = valves[nextValveName]!!
            } else {
                nextValve01 = Valve01(nextValveName)
                valves[nextValveName] = nextValve01
            }
            if (valve01.connectedValves.none { v -> v.name == nextValveName }) {
                valve01.connectedValves.add(nextValve01)
            }
            if (nextValve01.connectedValves.none { v -> v.name == name }) {
                nextValve01.connectedValves.add(valve01)
            }
        }
    }
}

private class Valve01 (val name: String,
               var rate: Int = 0,
               var connectedValves: MutableList<Valve01> = mutableListOf<Valve01>())  {

    private var valve01Distances: MutableMap<Int, MutableList<Valve01>> = HashMap<Int, MutableList<Valve01>>()

    override fun toString(): String {
        return "$name: $rate pressure / minute"
    }

    fun output(secondsRemaining: Int): Int {
        return secondsRemaining * rate
    }

    fun calculateHops() {
        var currentValvesAtHopCount: MutableList<Valve01> = mutableListOf(this)
        val processedValves: MutableList<String> = mutableListOf(this.name)
        var hopCount: Int = 0
        valve01Distances[0] = currentValvesAtHopCount
        while (processedValves.size < valves.size) {
            hopCount++
            valve01Distances[hopCount] = mutableListOf<Valve01>()
            for (valve in currentValvesAtHopCount) {
                for (nextValve in valve.connectedValves) {
                    if (!processedValves.contains(nextValve.name)) {
                        valve01Distances[hopCount]?.add(nextValve)
                        processedValves.add(nextValve.name)
                    }
                }
            }
            currentValvesAtHopCount = valve01Distances[hopCount]!!
        }
    }

    fun maxPressureIn(secondsRemaining: Int, currentOutput: Int, openValves: MutableList<String>): Int {
        val valve01ValueMap: MutableMap<Valve01, Int>  = valveValueMap(openValves, secondsRemaining)

        if (secondsRemaining <= 0 || openValves.size == valves.size || valve01ValueMap.isEmpty()) {
            return currentOutput
        }

        return valve01ValueMap.map {

            val travelDistance: Int = valve01Distances.filter { valveDistance -> valveDistance.value.contains(it.key) }.keys.first()

            val newOpenValves: MutableList<String> = openValves.toMutableList()
            if (!newOpenValves.contains(it.key.name)) {
                newOpenValves.add(it.key.name)
            }

            val newSecondsRemaining = secondsRemaining - (travelDistance + 1)
            val newOutput = currentOutput + it.key.output(newSecondsRemaining)

            it.key.maxPressureIn(newSecondsRemaining, newOutput, newOpenValves)
        }.max()

    }

    private fun valveValueMap(openValves: MutableList<String>, secondsLeft: Int): MutableMap<Valve01, Int> {
        val valve01ValueMap: MutableMap<Valve01, Int> = HashMap<Valve01, Int>()

        valve01Distances.keys.forEach { distance ->
            valve01Distances[distance]?.forEach { valve ->
                val value = valve.output(secondsLeft - (distance + 1))
                if (!openValves.contains(valve.name) && value > 0) {
                    valve01ValueMap[valve] = value
                }
            }
        }
        return valve01ValueMap
    }
}
