import Common.Companion.indexesOf

//private const val FILENAME: String = "day23-sample-small.txt"
//private const val FILENAME: String = "day23-sample-medium.txt"
private const val FILENAME: String = "day23.txt"

private const val ELF = '#'
private const val NORTH = 'N'
private const val SOUTH = 'S'
private const val WEST = 'W'
private const val EAST = 'E'
private val MOVE_ORDER = listOf(NORTH, SOUTH, WEST, EAST)

// 1. each elf considers their position
// 2a. if no adjacent elves, do nothing
// 2b. if no norther elves, move N
// 2c. if no southern elves, move S
// 2d. if no western elves, move W
// 2e. if no eastern elves, move E
// 3. All elves move simultaneously if not in conflict with other elves
// 4. On each subsequent round N/S/W/E rotates by one ex. S/W/E/N

 fun main() {
     val input = Common.readFile(FILENAME)

     val elves = processMap(input)
     var elfMoved = true
     var rounds = 0

     while(elfMoved) {
         elfMoved = false

         val proposedMoves: MutableList<ElfPosition> = mutableListOf()
         val proposedMovers: MutableList<ElfPosition> = mutableListOf()
         for(elf in elves) {
             elf.setProposedMove(elves, MOVE_ORDER[rounds % MOVE_ORDER.size])
             if (elf != elf.proposedMove) {
                 if (!proposedMoves.contains(elf.proposedMove)) {
                     proposedMovers.add(elf)
                 }
                 proposedMoves.add(elf.proposedMove)
             }
         }
         for(elf in proposedMovers) {
             if(!elf.equals(elf.proposedMove) && proposedMoves.indexesOf(elf.proposedMove).size == 1) {
                 elfMoved = true
                 elf.executeMove()
             }
         }
         rounds++
     }

     val answer = (elves.maxOf { e -> e.x } - elves.minOf { e -> e.x} + 1) *
             (elves.maxOf { e -> e.y } - elves.minOf { e -> e.y } + 1) - elves.size

     println("Rounds: ${rounds}\nEmpty spaces in minimum rectangle: ${answer}\n")
}

private fun processMap(map: List<String>): Set<ElfPosition> {
    val elves: MutableSet<ElfPosition> = mutableSetOf()
    for ((y, line) in map.withIndex()) {
        for ((x, spot) in line.withIndex()) {
            if (spot == ELF) {
                elves.add(ElfPosition(x, y))
            }
        }
    }
    return elves
}

private class ElfPosition(var x: Int, var y: Int) {

    lateinit var proposedMove: ElfPosition

    val movePositions = listOf(::northernPositions, ::southernPositions, ::westernPositions, ::easternPositions)
    val moveLocation = listOf(::northMove, ::southMove, ::westMove, ::eastMove)

    fun executeMove() {
        this.x = proposedMove.x
        this.y = proposedMove.y
    }

    fun setProposedMove(elves: Set<ElfPosition>, firstDirection: Char) {
        proposedMove = this

        if (elves.intersect(
                northernPositions()
                    .union(southernPositions())
                    .union(westernPositions())
                    .union(easternPositions())).isEmpty()) {
            return
        }

        val functionOffset = when (firstDirection) {
            SOUTH -> {
                1
            }

            WEST -> {
                2
            }

            EAST -> {
                3
            }

            else -> {
                0
            }
        }

        for(i in 0..3) {
            if (areaClear(elves, movePositions[(i + functionOffset) % 4]())) {
                proposedMove = moveLocation[(i + functionOffset) % 4]()
                return
            }
        }
    }

    private fun areaClear(elves: Set<ElfPosition>, spaces: Set<ElfPosition>): Boolean {
        return elves.intersect(spaces).isEmpty()
    }

    fun northernPositions(): Set<ElfPosition> {
        return setOf(
            ElfPosition(x - 1, y - 1),
            ElfPosition(x, y - 1),
            ElfPosition(x + 1, y - 1))
    }

    fun northMove(): ElfPosition {
        return ElfPosition(x, y - 1)
    }

    fun southernPositions(): Set<ElfPosition> {
        return setOf(
            ElfPosition(x - 1, y + 1),
            ElfPosition(x, y + 1),
            ElfPosition(x + 1, y + 1))
    }

    fun southMove(): ElfPosition {
        return ElfPosition(x, y + 1)
    }

    fun westernPositions(): Set<ElfPosition> {
        return setOf(
            ElfPosition(x - 1, y - 1),
            ElfPosition(x - 1, y),
            ElfPosition(x - 1, y + 1))
    }

    fun westMove(): ElfPosition {
        return ElfPosition(x - 1, y)
    }

    fun easternPositions(): Set<ElfPosition> {
        return setOf(
            ElfPosition(x + 1, y - 1),
            ElfPosition(x + 1, y),
            ElfPosition(x + 1, y + 1))
    }

    fun eastMove(): ElfPosition {
        return ElfPosition(x + 1, y)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ElfPosition) return false

        return (x == other.x) && (y == other.y)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}