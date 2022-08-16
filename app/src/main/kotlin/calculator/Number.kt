package calculator

import java.math.BigInteger

/**
 * Wrapper class for the [BigInteger] class.
 */
@Suppress("MemberVisibilityCanBePrivate")
data class Number(val value: BigInteger) : Calculator.Operand, CalculationResult {
    /**
     * Converts number to a string representation.
     */
    override fun toString() = value.toString()


    /**
     * Changes the sign of a number.
     */
    operator fun unaryMinus() = Number(-value)

    /**
     * Adds two numbers and returns a new one.
     */
    operator fun plus(other: Number) = Number(value + other.value)

    /**
     * Subtracts two numbers and returns a new one.
     */
    operator fun minus(other: Number) = Number(value - other.value)

    /**
     * Multiplies two numbers and returns a new one.
     */
    operator fun times(other: Number) = Number(value * other.value)

    /**
     * Divides two numbers and returns a new one.
     *
     * @throws ArithmeticException If division by zero occurs or division is undefined
     */
    operator fun div(other: Number) =
        when {
            value == BigInteger.ZERO && other.value == BigInteger.ZERO -> {
                throw ArithmeticException("Division undefined")
            }

            other.value == BigInteger.ZERO -> {
                throw ArithmeticException("Division by zero")
            }

            else -> Number(value / other.value)
        }

    /**
     * Divides two numbers and returns the remainder of the division.
     */
    operator fun rem(other: Number) =
        when {
            value == BigInteger.ZERO && other.value == BigInteger.ZERO -> {
                throw ArithmeticException("Division undefined")
            }

            other.value == BigInteger.ZERO -> {
                throw ArithmeticException("Division by zero")
            }

            else -> Number(value % other.value)
        }

    /**
     * Raises the given number to the power of [y].
     *
     * @throws ArithmeticException If the [y] value is negative or greater than [Int.MAX_VALUE].
     */
    infix fun pow(y: Number): Number {
        if (y.value < BigInteger.ZERO) {
            throw ArithmeticException("Illegal x^y for y < 0: y = $y")
        } else {
            val intValue = try {
                y.value.intValueExact()
            } catch (e: ArithmeticException) {
                throw ArithmeticException("Illegal x^y for y > ${Int.MAX_VALUE}: y = $y")
            }

            return Number(value.pow(intValue))
        }
    }

    /**
     * Returns the factorial of a given number.
     *
     * @throws ArithmeticException If the current number is less than zero.
     */
    fun factorial() =
        when {
            value < BigInteger.ZERO -> throw ArithmeticException("Illegal x! for x < 0: x = $this")
            value == BigInteger.ZERO -> Number(BigInteger.ONE)
            else -> Number(factorial(value))
        }


    private tailrec fun factorial(x: BigInteger, temp: BigInteger = BigInteger.ONE): BigInteger =
        when {
            x <= BigInteger.ONE -> temp
            else -> factorial(x - BigInteger.ONE, x * temp)
        }
}
