//private const val FILENAME: String = "day21-sample.txt"
private const val FILENAME: String = "day21.txt"

private var mathMonkeys: MutableMap<String, IMonkey> = mutableMapOf<String, IMonkey>()

fun main() {
    println("Part 1:\n")
    val input = Common.readFile(FILENAME)

    for (s in input) {
        val pieces = s.split(' ')
        val name = pieces[0].substring(0, pieces[0].length - 1)
        if (pieces.size == 2) {
            mathMonkeys[name] = ValueMonkey(name, pieces[1].toLong())
        } else {
            mathMonkeys[name] = OperatorMonkey(name, pieces[1], pieces[3], pieces[2])
        }
    }

    println("root will yell out: ${ mathMonkeys["root"]!!.yell() }\n")

    println("Part 2:\n")
    println("root will yell out: ${ mathMonkeys["root"]!!.yellAsString() }\n")
}

private interface IMonkey {
    fun yell(): Long
    fun yellAsString(): String
}
private class ValueMonkey(val name: String, val value: Long): IMonkey {
    override fun yell(): Long {
        return value
    }

    override fun yellAsString(): String {
        return if (name == "humn") {
            "MY_VALUE"
        } else {
            value.toString()
        }
    }
}

private class OperatorMonkey(val name: String, val leftMonkeyName: String, val rightMonkeyName: String, val method: String): IMonkey {
    override fun yell(): Long {
        val leftValue = mathMonkeys[leftMonkeyName]!!.yell()
        val rightValue = mathMonkeys[rightMonkeyName]!!.yell()

        return when (method) {
            "-" -> leftValue - rightValue
            "*" -> leftValue * rightValue
            "/" -> leftValue / rightValue
            else -> leftValue + rightValue
        }
    }

    override fun yellAsString(): String {
        val leftValue = mathMonkeys[leftMonkeyName]!!.yellAsString()
        val rightValue = mathMonkeys[rightMonkeyName]!!.yellAsString()

        return if (name == "root") {
            println("Left Value: $leftValue")
            println("Right Value: $rightValue")
            (leftValue == rightValue).toString()
        } else if (isNumeric(leftValue) && isNumeric(rightValue)) {
            val lVal = leftValue.toLong()
            val rVal = rightValue.toLong()
            when (method) {
                "-" -> (lVal - rVal).toString()
                "*" -> (lVal * rVal).toString()
                "/" -> (lVal / rVal).toString()
                else -> (lVal + rVal).toString()
            }
        } else {
            "($leftValue $method $rightValue)"
        }
    }

    private fun isNumeric(s: String): Boolean {
        return s.all { char -> char.isDigit() }
    }
}