private const val DIR_CONTENT_LIMIT: Int = 100000
private const val TOTAL_SPACE: Int = 70000000
private const val SPACE_FOR_UPDATE: Int = 30000000

private fun main() {
    val input = Common.readFile("day07-part01.txt")

    val rootDir: Dir = Dir("/")
    var currentDir: Dir = rootDir
    var addToCurrentDir: Boolean = false

    for ((index, line) in input.withIndex()) {
        if (index == 0) {
            continue
        } else if (line.startsWith("$")) {
            if (line.startsWith("$ ls")) {
                addToCurrentDir = true
            } else if (line.startsWith("$ cd")) {
                addToCurrentDir = false
                val destination = line.split(" ")[2]
                currentDir = if (destination == ".." && currentDir.parentDir != null) {
                    currentDir.parentDir!!
                } else {
                    currentDir.dirs.find { it.dirName == destination }!!
                }
            }
        } else if (addToCurrentDir) {
            val parts = line.split(" ")
            if (parts[0] == "dir") {
                val newDir: Dir = Dir(parts[1])
                currentDir.dirs.add(newDir)
                newDir.parentDir = currentDir
            } else {
                val fileSize: Int = parts[0].toInt()
                currentDir.totalFileSize += fileSize
                currentDir.addToParent(fileSize)
            }
        }
    }
    println("Part 1:\n")
    println("Total size of target directories: ${totalTargetDirSizes(rootDir)}\n")
    println("Part 2:\n")
    println("Size of directory to delete: ${findSmallestDirectoryToDelete(rootDir)}")
}

private fun totalTargetDirSizes(dir : Dir) : Int {
    var subtotal: Int = 0

    dir.dirs.forEach {
        subtotal += totalTargetDirSizes(it)
    }
    if (dir.totalFileSize <= DIR_CONTENT_LIMIT) {
        subtotal += dir.totalFileSize
    }
    return subtotal
}

private fun findSmallestDirectoryToDelete(rootDir : Dir) : Int {
    val spaceNeeded = SPACE_FOR_UPDATE - (TOTAL_SPACE - rootDir.totalFileSize)

    var dirList : MutableList<Dir> = createDirList(rootDir)
    return dirList.sortedBy { it.totalFileSize }.first { it.totalFileSize > spaceNeeded }.totalFileSize
}

private fun createDirList(dir : Dir) : MutableList<Dir> {
    var dirList : MutableList<Dir> = dir.dirs.toMutableList()
    dir.dirs.forEach {
        dirList.addAll(createDirList(it))
    }
    return dirList
}

class Dir (name: String){
    val dirName: String = name
    var parentDir: Dir? = null
    var dirs: MutableList<Dir> = ArrayList<Dir>()
    var totalFileSize: Int = 0

    fun addToParent(size: Int) {
        if (parentDir != null) {
            parentDir!!.totalFileSize += size
            parentDir!!.addToParent(size)
        }
    }
}