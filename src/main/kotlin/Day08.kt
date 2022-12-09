import java.lang.Integer.max

//const val FILENAME: String = "day08-sample.txt"
private const val FILENAME: String = "day08-part01.txt"
private const val VISIBLE: Char = 'V'
private const val HIDDEN: Char = 'H'

private fun main() {
    val treeHeights: List<List<Int>> = Common.readFileToTwoDIntGrid(FILENAME)
    val width: Int = treeHeights [0].size
    val depth: Int = treeHeights.size

    val treeVisibility: MutableList<MutableList<Char>> = MutableList(depth) {MutableList(width) { HIDDEN } }
    treeVisibility[0] = MutableList(width) {'V'}
    treeVisibility[treeVisibility.size - 1] = MutableList(width) {'V'}
    treeVisibility.forEach {
        it[0] = VISIBLE
        it[it.size - 1] = VISIBLE
    }

    val treeScenicScore: MutableList<MutableList<Int>> = MutableList(depth) {MutableList(width) { 0 } }
    var highScenicScore: Int = 0

    treeHeights.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, height ->
            if (treeVisibility[rowIndex][columnIndex] == HIDDEN) {
                val column: List<Int> = treeVisibility.indices.map {i -> treeHeights[i][columnIndex] }
                val left: List<Int> = row.subList(0, columnIndex).reversed()
                val right: List<Int> = row.subList(columnIndex + 1, width)
                val above: List<Int> = column.subList(0, rowIndex).reversed()
                val below: List<Int> = column.subList(rowIndex + 1, depth)

                val tallestSurrounding: List<Int> = listOf(
                    left.max(),
                    right.max(),
                    above.max(),
                    below.max())                 // BELOW

                if (tallestSurrounding.min() < height)
                    treeVisibility[rowIndex][columnIndex] = VISIBLE

                val currentScenicScore = numVisibleTrees(left, height) *
                        numVisibleTrees(right, height) *
                        numVisibleTrees(above, height) *
                        numVisibleTrees(below, height)

                highScenicScore = max(highScenicScore, currentScenicScore)
            }
        }
    }

    var visibleCount: Int = 0
    treeVisibility.forEach { r -> r.forEach { ch -> if (ch == VISIBLE) { visibleCount++ } }}

    println("Part 1:\n")
    println("Visible tree count: $visibleCount\n")

    println("Part 2:\n")
    println("Highest scenic score: $highScenicScore\n")
}

private fun numVisibleTrees(heights: List<Int>, currentHeight: Int): Int {
    if (heights.isEmpty()) {
        return 0
    }

    val tallTreeIndex: Int = heights.indexOfFirst { it >= currentHeight }

    if (tallTreeIndex == -1) {
        return heights.size
    } else {
        return tallTreeIndex + 1
    }
}