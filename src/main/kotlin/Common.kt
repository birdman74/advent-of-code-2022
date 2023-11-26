import java.io.File
import java.nio.file.Paths

class Common {
    companion object {
        fun readFile(fileName: String): List<String> {
            println("Working directory: " + Paths.get("").toAbsolutePath().toString())
            val fullFileName = "./input/$fileName"
            println("Input File Name: $fullFileName\n")
            return File(fullFileName).readLines()
        }

        fun <E> Iterable<E>.indexesOf(e: E)
                = mapIndexedNotNull { index, elem -> index.takeIf{elem == e} }

        fun readFileToTwoDIntGrid(fileName: String): List<List<Int>> {
            val lines = readFile(fileName)
            val grid: MutableList<List<Int>> = ArrayList()

            lines.forEach { line ->
                grid.add(line.map { it.digitToInt() })
            }
            return grid
        }
    }
}
