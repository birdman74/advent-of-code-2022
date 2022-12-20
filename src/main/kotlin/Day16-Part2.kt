//private const val FILENAME: String = "day16-sample.txt"
private const val FILENAME: String = "day16.txt"

private var valves02: MutableMap<String, Valve02> = HashMap<String, Valve02>()

fun main() {
    initValves02()

    val initialTimeLeft: Int = 26
    val initialLocation: String = "AA"
    val openValves: MutableList<String> = mutableListOf<String>()

    valves02.values.forEach {
        if (it.rate == 0) {
            openValves.add(it.name)
        }
        it.calculateHops()
    }

    println("Max pressure released in $initialTimeLeft minutes: ${ 
        valves02["AA"]?.maxPressureIn(initialTimeLeft, initialLocation, initialTimeLeft, initialLocation, 0, openValves)
    }\n")
}

private fun initValves02() {
    val input = Common.readFile(FILENAME)

    valves02 = HashMap<String, Valve02>()

    input.forEach { line ->
        val pieces = line.split(" ")
        val name: String = pieces[1]
        val rate: Int = pieces[4].split("=")[1].split(";")[0].toInt()
        val nextValveNames = pieces.subList(9, pieces.size).map { it.substring(0, 2) }

        val valve01: Valve02
        if (valves02.keys.contains(name)) {
            valve01 = valves02[name]!!
        } else {
            valve01 = Valve02(name)
            valves02[name] = valve01
        }
        valve01.rate = rate

        for (nextValveName in nextValveNames) {
            var nextValve02: Valve02
            if (valves02.keys.contains(nextValveName)) {
                nextValve02 = valves02[nextValveName]!!
            } else {
                nextValve02 = Valve02(nextValveName)
                valves02[nextValveName] = nextValve02
            }
            if (valve01.connectedValves.none { v -> v.name == nextValveName }) {
                valve01.connectedValves.add(nextValve02)
            }
            if (nextValve02.connectedValves.none { v -> v.name == name }) {
                nextValve02.connectedValves.add(valve01)
            }
        }
    }
}

private class Valve02 (val name: String,
                       var rate: Int = 0,
                       var connectedValves: MutableList<Valve02> = mutableListOf<Valve02>())  {

    private var valve01Distances: MutableMap<Int, MutableList<Valve02>> = HashMap<Int, MutableList<Valve02>>()

    override fun toString(): String {
        return "$name: $rate pressure / minute"
    }

    fun output(secondsRemaining: Int): Int {
        return secondsRemaining * rate
    }

    fun calculateHops() {
        var currentValvesAtHopCount: MutableList<Valve02> = mutableListOf(this)
        val processedValves: MutableList<String> = mutableListOf(this.name)
        var hopCount: Int = 0
        valve01Distances[0] = currentValvesAtHopCount
        while (processedValves.size < valves02.size) {
            hopCount++
            valve01Distances[hopCount] = mutableListOf<Valve02>()
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

    fun maxPressureIn(mySecondsRemaining: Int, myLocation: String,
                      elSecondsRemaining: Int, elLocation: String,
                      totalOutput: Int, openValves: MutableList<String>): Int {

        val valueMap: MutableMap<Valve02, Int>  = valveValueMap(openValves, mySecondsRemaining)

        if ((mySecondsRemaining <= 0 && elSecondsRemaining <= 0) || openValves.size == valves02.size || valueMap.isEmpty()) {
            return totalOutput
        }

        val myMove: Boolean = myLocation == name

        return valueMap.map {
            val newValve: Valve02 = it.key
            val newOpenValves: MutableList<String> = openValves.toMutableList()
            if (!newOpenValves.contains(newValve.name)) {
                newOpenValves.add(newValve.name)
            }

            val travelDistance: Int = valve01Distances.filter { valveDistance ->
                valveDistance.value.contains(newValve)
            }.keys.first()

            if (myMove) {
                val newSecondsRemaining = mySecondsRemaining - (travelDistance + 1)
                val newOutput = totalOutput + newValve.output(newSecondsRemaining)

                if (newSecondsRemaining > elSecondsRemaining) {
                    newValve.maxPressureIn(
                        newSecondsRemaining, newValve.name,
                        elSecondsRemaining, elLocation,
                        newOutput, newOpenValves
                    )
                } else {
                    valves02[elLocation]?.maxPressureIn(
                        newSecondsRemaining, newValve.name,
                        elSecondsRemaining, elLocation,
                        newOutput, newOpenValves
                    )!!
                }
            } else {
                val newSecondsRemaining = elSecondsRemaining - (travelDistance + 1)
                val newOutput = totalOutput + newValve.output(newSecondsRemaining)

                if (newSecondsRemaining > mySecondsRemaining) {
                    newValve.maxPressureIn(
                        mySecondsRemaining, myLocation,
                        newSecondsRemaining, newValve.name,
                        newOutput, newOpenValves
                    )
                } else {
                    valves02[myLocation]?.maxPressureIn(
                        mySecondsRemaining, myLocation,
                        newSecondsRemaining, newValve.name,
                        newOutput, newOpenValves
                    )!!
                }
            }
        }.max()
    }

    private fun valveValueMap(openValves: MutableList<String>, secondsLeft: Int): MutableMap<Valve02, Int> {
        val valve01ValueMap: MutableMap<Valve02, Int> = HashMap<Valve02, Int>()

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
