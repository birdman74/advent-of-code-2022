import java.io.File

class Common {
    companion object {
        fun readFile(fileName: String): List<String> {
            val fullFileName = "./input/$fileName"
            println("Input File Name: $fullFileName\n")
            return File(fullFileName).readLines()
        }

        fun <E> Iterable<E>.indexesOf(e: E)
                = mapIndexedNotNull { index, elem -> index.takeIf{elem == e} }

        fun readFileToTwoDIntGrid(fileName: String): List<List<Int>> {
            val lines = readFile(fileName)
            var grid: MutableList<List<Int>> = ArrayList<List<Int>>()

            lines.forEach { line ->
                grid.add(line.map { it.digitToInt() })
            }
            return grid
        }
    }
}
