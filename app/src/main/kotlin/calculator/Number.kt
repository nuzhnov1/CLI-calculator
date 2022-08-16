package calculator

import ch.obermuhlner.math.big.BigDecimalMath.factorial
import ch.obermuhlner.math.big.BigDecimalMath.pow
import java.math.BigDecimal
import java.math.MathContext

/**
 * Wrapper class for the [BigDecimal] class.
 * Arithmetic operations on this class can lead to an overflow exception
 * (an instance of the [ArithmeticException] class).
 */
@Suppress("MemberVisibilityCanBePrivate")
data class Number(val value: BigDecimal) : Calculator.Operand, CalculationResult {
    /**
     * Checks whether number is an integer.
     */
    val isInteger: Boolean
        get() = value.signum() == 0 || value.scale() <= 0 || value.stripTrailingZeros().scale() <= 0


    /**
     * Converts number to a string representation with a precision of 15 and
     * the maximum number of trailing zeros is 9.
     */
    override fun toString() = toString(15, 9)

    /**
     * Converts number to a string representation with an arbitrary [precision] and
     * the maximum number of trailing zeros - [maxTrailingZeros].
     */
    fun toString(precision: Int, maxTrailingZeros: Int): String {
        val minScale = -maxTrailingZeros
        val maxValue = BigDecimal.TEN.pow(precision)
        val tempValue = value.round(MathContext(precision)).stripTrailingZeros()

        return if (tempValue < maxValue && tempValue.scale() in minScale..0) {
            tempValue.toPlainString()
        } else {
            tempValue.toString().replace("E", "*10^")
        }
    }


    /**
     * Changes the sign of a number by rounding it to the precision specified by the [mc].
     */
    fun negate(mc: MathContext) = Number(value.negate(mc))

    /**
     * Adds two numbers and returns a new one rounded to the precision specified in the [mc].
     */
    fun add(other: Number, mc: MathContext) = Number(value.add(other.value, mc))

    /**
     * Subtracts two numbers and returns a new one rounded to the precision specified in the [mc].
     */
    fun subtract(other: Number, mc: MathContext) = Number(value.subtract(other.value, mc))

    /**
     * Multiplies two numbers and returns a new one rounded to the precision specified in the [mc].
     */
    fun multiply(other: Number, mc: MathContext) = Number(value.multiply(other.value, mc))

    /**
     * Divides two numbers and returns a new one rounded to the precision specified in the [mc].
     *
     * @throws ArithmeticException If division by zero occurs or division is undefined
     */
    fun divide(other: Number, mc: MathContext) = Number(value.divide(other.value, mc))

    /**
     * Raises the given number to the power of [y], rounding it to the precision specified in the [mc].
     *
     * @throws ArithmeticException If the current number is less than zero and [y] is not an integer,
     * @throws UnsupportedOperationException If the [mc] has unlimited precision.
     * or if the current number is zero but [y] is less than zero.
     */
    fun pow(y: Number, mc: MathContext): Number {
        return when {
            value < BigDecimal.ZERO && !y.isInteger -> {
                throw ArithmeticException(
                    "Illegal x^y for x < 0 and non-integer y: " +
                    "x = $this; y = $y"
                )
            }

            value.signum() == 0 && y.value < BigDecimal.ZERO -> {
                throw ArithmeticException("Illegal x^y for x = 0 and y < 0: y = $y")
            }

            else -> Number(pow(value, y.value, mc))
        }
    }

    /**
     * Returns the factorial of a given number, rounding it to the precision specified in the [mc].
     * For a fractional numbers, this function applies the gamma function.
     *
     * @throws ArithmeticException If the current number is an integer and less than zero.
     * @throws UnsupportedOperationException If the current number is not an integer and
     * the [mc] has unlimited precision.
     */
    fun factorial(mc: MathContext): Number {
        if (value < BigDecimal.ZERO && isInteger) {
            throw ArithmeticException("Illegal x! for x < 0, where x is integer: x = $this")
        } else {
            return Number(factorial(value, mc))
        }
    }

    /**
     * Returns the result of a division the current number by 100,
     * rounding it to the precision specified in the [mc].
     */
    fun percent(mc: MathContext) = divide(Number(BigDecimal(100)), mc)
}
