//private const val FILENAME: String = "day13-sample.txt"
private const val FILENAME: String = "day13.txt"

private val OPEN: Char = '['
private val CLOSE: Char = ']'

fun main() {
//    part01()
    part02()
}

private fun part01() {
    println("Part 1:\n")
    val input = Common.readFile(FILENAME)

    val pairs: List<List<MessagePacketList>> =
        input.chunked(3).map { pairs -> pairs.subList(0, 2).map { value -> MessagePacketList(value) } }

    var indexSum: Int = pairs.mapIndexed { index, messagePacketLists ->
        if (messagePacketLists[0].before(messagePacketLists[1])) {
            index + 1
        } else {
            0
        }
    }.sum()

    println("Index Sum of Ordered Pairs: $indexSum\n")
}

private fun part02() {
    println("Part 2:\n")
    val input = Common.readFile(FILENAME).toMutableList()

    while (input.contains("")) {
        input.remove("")
    }
    val dividerPackets: MutableList<String> = mutableListOf("[[2]]", "[[6]]")
    input.addAll(dividerPackets)

    val messagePackages: MutableList<MessagePacketList> = input.map {  value -> MessagePacketList(value) }.toMutableList()
    val sortedPackets: MutableList<MessagePacketList> = sortPackets(messagePackages)

    var separatorPacketIndexProduct: Int = 1
    sortedPackets.forEachIndexed { index, messagePacketList ->
        if (messagePacketList.inputValue in dividerPackets) {
            separatorPacketIndexProduct *= (index + 1)
        }
    }

    println("Product of separator packet indices: $separatorPacketIndexProduct\n")
}

private fun sortPackets(packetList: MutableList<MessagePacketList>): MutableList<MessagePacketList> {
    var sortedPacketList: MutableList<MessagePacketList> = mutableListOf()

    while (packetList.size > 0) {
        var minPacketIndex: Int = 0
        for (i in 1 until packetList.size) {
            if (!packetList[minPacketIndex].before(packetList[i])) {
                minPacketIndex = i
            }
        }
        sortedPacketList.add(packetList[minPacketIndex])
        packetList.removeAt(minPacketIndex)
    }
    return sortedPacketList
}

class MessagePacketList(inputValue: String) {
    val inputValue: String = inputValue
    var comparableValue: MutableList<Any> = mutableListOf<Any>()

    init {
        var listTracker: MutableList<MutableList<Any>> = mutableListOf<MutableList<Any>>( comparableValue )
        var currentListIndex: Int = 0
        var intValuesStart = -1
        for (i in 1 until inputValue.length) {
            when (inputValue[i]) {
                OPEN -> {
                    if (intValuesStart > -1) {
                        listTracker[currentListIndex].addAll(parseInts(inputValue, intValuesStart, i))
                    }

                    var newList: MutableList<Any> = mutableListOf<Any>()
                    listTracker[currentListIndex].add(newList)
                    listTracker.add(newList)
                    currentListIndex++
                    intValuesStart = -1
                }
                CLOSE -> {
                    if (intValuesStart > -1) {
                        listTracker[currentListIndex].addAll(parseInts(inputValue, intValuesStart, i))
                    }

                    listTracker.removeLast()
                    currentListIndex--
                    intValuesStart = -1
                }
                else -> {
                    if (inputValue[i].isDigit() && intValuesStart == -1) {
                        intValuesStart = i
                    }
                }
            }
        }
    }

    private fun parseInts(str: String, beginIndex: Int, endIndex: Int): List<Int> {
        var intStr = str.substring(beginIndex, endIndex)
        if (intStr[0] == ',') {
            intStr = intStr.substringAfter(',')
        }
        if (intStr.last() == ',') {
            intStr = intStr.substringBeforeLast(',')
        }
        return intStr.split(",").map { it -> it.toInt() }
    }

    private fun trimChar(str: String, ch: Char): String {
        var returnVal: String = ""

        if (str == null || str.isEmpty()) {
            return str
        }

        if (str.startsWith(",")) {
            returnVal = str.substring(1)
        }
        if (str.endsWith(",")) {
            returnVal = str.substring(0 until returnVal.length - 1)
        }
        return returnVal
    }

    /*
    Rules:
     1. if comparing integers , lower should be on left
     2. if comparing lists, compare [Nth] items with #1
        if all items are == but list sizes differ, lower list.size should be on left
     3.  if i <> list, i -> listOf(i).
     */
    fun before(rightMessagePacketList: MessagePacketList): Boolean {
        val compareValue: Int = compareLists(comparableValue, rightMessagePacketList.comparableValue)
        return compareValue < 0
    }

    private fun compareLists(leftList: MutableList<*>, rightList: MutableList<*>): Int {
        leftList.forEachIndexed { index, leftItem ->
            try {
                val rightItem = rightList[index]
                if (leftItem is Int && rightItem is Int) {
                    if (leftItem < rightItem) {
                        return -1
                    } else if (leftItem > rightItem) {
                        return 1
                    }
                } else if (leftItem is MutableList<*> && rightItem is MutableList<*>) {
                    val retValue: Int = compareLists(leftItem, rightItem)
                    if (retValue != 0) {
                        return retValue
                    }
                } else {
                    if (leftItem is Int) {
                        val retValue: Int = compareLists(mutableListOf(leftItem), rightItem as MutableList<*>)
                        if (retValue != 0) {
                            return retValue
                        }
                    } else {
                        val retValue: Int = compareLists(leftItem as MutableList<*>, mutableListOf(rightItem))
                        if (retValue != 0) {
                            return retValue
                        }
                    }
                }
            } catch (e: IndexOutOfBoundsException) {
                return 1
            }
        }
        if (leftList.size < rightList.size) {
            return -1
        }
        return 0
    }
}