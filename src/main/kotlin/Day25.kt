import java.math.BigInteger

//private const val FILENAME: String = "day25-sample.txt"
private const val FILENAME: String = "day25.txt"

private const val TWO = '2'
private const val ONE = '1'
private const val ZERO = '0'
private const val MINUS = '-'
private const val DOUBLE_MINUS = '='

fun main() {
    val input = Common.readFile(FILENAME)
    var decimalTotal = BigInteger.ZERO

    for (snafu in input) {
        decimalTotal += convertSnafu(snafu)
        print("$snafu == ${convertSnafu(snafu)}\n")
    }
    print("\nDecimal total: ${decimalTotal}\n")
    print("\nSnafu Code: ${toSnafu(decimalTotal)}")
}

private fun convertSnafu(snafu: String): BigInteger {
    var returnVal = BigInteger.ZERO
    val highestPlace = snafu.length - 1
    val five = BigInteger.valueOf(5)

    for (i in highestPlace downTo 0) {
        val m = five.pow(i)
        when(snafu[snafu.length - 1 - i]) {
            TWO -> returnVal += m.times(BigInteger.TWO)
            ONE -> returnVal += m
            MINUS -> returnVal -= m
            DOUBLE_MINUS -> returnVal -= m.times(BigInteger.TWO)
            else -> continue
        }
    }

    return returnVal
}

private fun toSnafu(decimal: BigInteger) : String {
    val (retValue, _) = recurseSnafu(decimal, 0, BigInteger.ZERO..BigInteger.ZERO)

    return retValue
}

private fun recurseSnafu(decimal: BigInteger,
                         power: Int,
                         prevRange: ClosedRange<BigInteger>): Pair<String, BigInteger> {

    val five = BigInteger.valueOf(5)
    val oneThisPower = five.pow(power)
    val twoThisPower = oneThisPower.times(BigInteger.TWO)

    val oneResult = decimal - oneThisPower
    val twoResult = decimal - twoThisPower

    if (prevRange.contains(oneResult)) {
        return Pair("1", oneResult)
    } else if (prevRange.contains(twoResult)) {
        return Pair("2", twoResult)
    } else {
        val (snafuDigits, remainder) =
            recurseSnafu(decimal, power + 1,
                prevRange.start - twoThisPower..prevRange.endInclusive + twoThisPower)

        return if (prevRange.contains(remainder - twoThisPower)) {
            Pair(snafuDigits + TWO, remainder - twoThisPower)
        } else if (prevRange.contains(remainder - oneThisPower)) {
            Pair(snafuDigits + ONE, remainder - oneThisPower)
        } else if (prevRange.contains(remainder + oneThisPower)) {
            Pair(snafuDigits + MINUS, remainder + oneThisPower)
        } else if (prevRange.contains(remainder + twoThisPower)) {
            Pair(snafuDigits + DOUBLE_MINUS, remainder + twoThisPower)
        } else {
            Pair(snafuDigits + ZERO, remainder)
        }
    }
}