// TODO: implement switching from radians to degrees and vice versa.
// TODO: implement sec, cosec, asec, acosec, sech, cosech, asech and acosech functions.

package calculator

import ch.obermuhlner.math.big.BigDecimalMath.*
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode


/**
 * Invokes the built-in calculator function by [name] with arguments in the [actualArgumentsStack].
 * Result of invocation is rounded to the precision specified in the [mc].
 *
 * @throws ExecutionException If the actual arguments count does not match the required one,
 * or unknown function is attempted to invoke.
 * @throws ArithmeticException If an error was occurred during the calculation.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
internal fun invokeFunction(
    name: String,
    actualArgumentsStack: ArrayDeque<Number>,
    mc: MathContext
): Number
{
    return when (name) {
        // Power functions:

        "sqrt" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            sqrt(actualArgumentsStack.removeFirst(), mc)
        }

        "cbrt" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            cbrt(actualArgumentsStack.removeFirst(), mc)
        }

        "root" -> {
            checkArgumentsCount(name, actualArgumentsStack, 2)

            val n = actualArgumentsStack.removeFirst()
            val x = actualArgumentsStack.removeFirst()

            root(x, n, mc)
        }

        "exp" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            exp(actualArgumentsStack.removeFirst(), mc)
        }

        // Logarithmic functions:

        "ln" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            ln(actualArgumentsStack.removeFirst(), mc)
        }

        "log" -> {
            checkArgumentsCount(name, actualArgumentsStack, 2)

            val x = actualArgumentsStack.removeFirst()
            val b = actualArgumentsStack.removeFirst()

            log(b, x, mc)
        }

        "log2" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            log2(actualArgumentsStack.removeFirst(), mc)
        }

        "log10" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            log10(actualArgumentsStack.removeFirst(), mc)
        }

        // Trigonometric functions:

        "sin" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            sin(actualArgumentsStack.removeFirst(), mc)
        }

        "cos" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            cos(actualArgumentsStack.removeFirst(), mc)
        }

        "tan" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            tan(actualArgumentsStack.removeFirst(), mc)
        }

        "cot" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            cot(actualArgumentsStack.removeFirst(), mc)
        }

        // Inverse trigonometric functions:

        "asin" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            asin(actualArgumentsStack.removeFirst(), mc)
        }

        "acos" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            acos(actualArgumentsStack.removeFirst(), mc)
        }

        "atan" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            atan(actualArgumentsStack.removeFirst(), mc)
        }

        "acot" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            acot(actualArgumentsStack.removeFirst(), mc)
        }

        // Hyperbolic functions:

        "sinh" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            sinh(actualArgumentsStack.removeFirst(), mc)
        }

        "cosh" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            cosh(actualArgumentsStack.removeFirst(), mc)
        }

        "tanh" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            tanh(actualArgumentsStack.removeFirst(), mc)
        }

        "coth" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            coth(actualArgumentsStack.removeFirst(), mc)
        }

        // Inverse hyperbolic functions:

        "asinh" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            asinh(actualArgumentsStack.removeFirst(), mc)
        }

        "acosh" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            acosh(actualArgumentsStack.removeFirst(), mc)
        }

        "atanh" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            atanh(actualArgumentsStack.removeFirst(), mc)
        }

        "acoth" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            acoth(actualArgumentsStack.removeFirst(), mc)
        }

        // Rounding functions:

        "ceil" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            ceil(actualArgumentsStack.removeFirst())
        }

        "floor" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            floor(actualArgumentsStack.removeFirst())
        }

        "round" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            round(actualArgumentsStack.removeFirst())
        }

        // Other functions:

        "abs" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            abs(actualArgumentsStack.removeFirst(), mc)
        }

        "fraction" -> {
            checkArgumentsCount(name, actualArgumentsStack, 1)
            fraction(actualArgumentsStack.removeFirst())
        }

        "hypot" -> {
            checkArgumentsCount(name, actualArgumentsStack, 2)

            val y = actualArgumentsStack.removeFirst()
            val x = actualArgumentsStack.removeFirst()

            hypot(x, y, mc)
        }


        else -> throw ExecutionException("Unknown function '$name'")
    }
}

/**
 * Compares the actual arguments count with the required one.
 *
 * @throws ExecutionException If the actual arguments count does not match the required one.
 */
private fun checkArgumentsCount(
    functionName: String,
    actualArgumentsStack: ArrayDeque<Number>,
    requiredCount: Int
)
{
    val actualCount = actualArgumentsStack.count()

    if (actualCount != requiredCount) {
        actualArgumentsStack.clear()

        throw ExecutionException(
            "The function '$functionName' expects $requiredCount arguments, " +
            "but given $actualCount"
        )
    }
}


// Power functions:

/**
 * Returns the square root of the [x], rounding it to the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value is less than zero.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun sqrt(x: Number, mc: MathContext): Number {
    if (x.value < BigDecimal.ZERO) {
        throw ArithmeticException("Illegal sqrt(x) for x < 0: x = $x")
    }

    return Number(sqrt(x.value, mc))
}

/**
 * Returns the cube root of the [x], rounding it to the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun cbrt(x: Number, mc: MathContext): Number {
    return if (x.value < BigDecimal.ZERO) {
        Number(root(x.value.negate(mc), BigDecimal(3), mc).negate(mc))
    } else {
        Number(root(x.value, BigDecimal(3), mc))
    }
}

/**
 * Returns the [n]th-root of the [x], rounding it to the precision specified in the [mc].
 *
 * @throws ArithmeticException In the following cases:
 * 1. If the [x] value is less than zero, and the [n] value is an integer and even number.
 * 2. If the [x] value is less than zero and the [n] value is not an integer.
 * 3. If the [x] value is zero and the [n] value is less than zero.
 * 4. If the [x] value is greater than zero and the [n] value is zero.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun root(x: Number, n: Number, mc: MathContext): Number {
    return when {
        x.value < BigDecimal.ZERO && n.isInteger -> {
            val rem = n.value
                .toBigIntegerExact()
                .mod(BigInteger.TWO)
                .intValueExact()

            if (rem != 0) {
                Number(root(x.value.negate(mc), n.value, mc).negate(mc))
            } else {
                throw ArithmeticException("Illegal root(x, n) for x < 0 and even n: x = $x; n = $n")
            }
        }

        x.value < BigDecimal.ZERO && !n.isInteger -> {
            throw ArithmeticException("Illegal root(x, n) for x < 0 and non-integer n: x = $x; n = $n")
        }

        x.value.signum() == 0 && n.value < BigDecimal.ZERO -> {
            throw ArithmeticException("Illegal root(x, n) for x = 0 and n < 0: n = $n")
        }

        x.value > BigDecimal.ZERO && n.value.signum() == 0 -> {
            throw ArithmeticException("Illegal root(x, n) for x > 0 and n = 0: x = $x")
        }

        else -> Number(root(x.value, n.value, mc))
    }
}

/**
 * Returns the exponent of the [x] value, i.e. the result of e^[x] expression,
 * where e is the Euler number.
 * The result of calculation is rounded to the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun exp(x: Number, mc: MathContext) = Number(exp(x.value, mc))


// Logarithmic functions:

/**
 * Returns the natural logarithm of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value is less than or equal to zero.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun ln(x: Number, mc: MathContext): Number {
    if (x.value <= BigDecimal.ZERO) {
        throw ArithmeticException("Illegal ln(x) for x <= 0: x = $x")
    }

    return Number(log(x.value, mc))
}

/**
 * Returns the logarithm of the [x] value with base [b], rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException In the following cases:
 * 1. If the [b] value is less than or equal to zero or one.
 * 2. If the [x] value is less than or equal to zero.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun log(b: Number, x: Number, mc: MathContext): Number {
    return when {
        b.value <= BigDecimal.ZERO -> {
            throw ArithmeticException("Illegal log(b, x) for b <= 0: b = $b")
        }

        b.value.compareTo(BigDecimal.ONE) == 0 -> {
            throw ArithmeticException("Illegal log(b, x) for b = 1")
        }

        x.value <= BigDecimal.ZERO -> {
            throw ArithmeticException("Illegal log(b, x) for x <= 0: x = $x")
        }

        else -> Number(log(x.value, mc) / log(b.value, mc))
    }
}

/**
 * Returns the logarithm of the [x] value with base 2, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value is less than or equal to zero.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun log2(x: Number, mc: MathContext): Number {
    if (x.value <= BigDecimal.ZERO) {
        throw ArithmeticException("Illegal log2(x) for x <= 0: x = $x")
    } else {
        return Number(log2(x.value, mc))
    }
}

/**
 * Returns the logarithm of the [x] value with base 10, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value is less than or equal to zero.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun log10(x: Number, mc: MathContext): Number {
    if (x.value <= BigDecimal.ZERO) {
        throw ArithmeticException("Illegal log10(x) for x <= 0: x = $x")
    } else {
        return Number(log10(x.value, mc))
    }
}


// Trigonometric functions:

/**
 * Returns the sine of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun sin(x: Number, mc: MathContext): Number {
    val result = sin(mod2pi(x.value, mc), mc)

    return when {
        isEquals(result, BigDecimal.ONE.negate(), mc) -> Number(BigDecimal.ONE.negate())
        isEquals(result, BigDecimal.ZERO, mc) -> Number(BigDecimal.ZERO)
        isEquals(result, BigDecimal.ONE, mc) -> Number(BigDecimal.ONE)
        else -> Number(result)
    }
}

/**
 * Returns the cosine of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun cos(x: Number, mc: MathContext): Number {
    val result = cos(mod2pi(x.value, mc), mc)

    return when {
        isEquals(result, BigDecimal.ONE.negate(), mc) -> Number(BigDecimal.ONE.negate())
        isEquals(result, BigDecimal.ZERO, mc) -> Number(BigDecimal.ZERO)
        isEquals(result, BigDecimal.ONE, mc) -> Number(BigDecimal.ONE)
        else -> Number(result)
    }
}

/**
 * Returns the tangent of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value is equals to pi * k / 2, where k is odd.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun tan(x: Number, mc: MathContext): Number {
    val sinResult = sin(x, mc).value
    val cosResult = cos(x, mc).value

    if (cosResult.signum() == 0) {
        throw ArithmeticException("Illegal tan(x) for x = pi * k / 2, where k is odd: x = $x")
    } else {
        return Number(sinResult.divide(cosResult, mc))
    }
}

/**
 * Returns the cotangent of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value is equals to pi * k, where k is an integer.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun cot(x: Number, mc: MathContext): Number {
    val sinResult = sin(x, mc).value
    val cosResult = cos(x, mc).value

    if (sinResult.signum() == 0) {
        throw ArithmeticException("Illegal cot(x) for x = pi * k, where k is an integer: x = $x")
    } else {
        return Number(cosResult.divide(sinResult, mc))
    }
}


// Inverse trigonometric functions:

/**
 * Returns the arcsine of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value does not belong to [-1, +1].
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun asin(x: Number, mc: MathContext): Number {
    return when {
        x.value < BigDecimal.ONE.negate(mc) -> {
            throw ArithmeticException("Illegal asin(x) for x < -1: x = $x")
        }

        x.value > BigDecimal.ONE -> {
            throw ArithmeticException("Illegal asin(x) for x > 1: x = $x")
        }

        else -> Number(asin(x.value, mc))
    }
}

/**
 * Returns the arccosine of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value does not belong to [-1, +1].
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun acos(x: Number, mc: MathContext): Number {
    return when {
        x.value < BigDecimal.ONE.negate(mc) -> {
            throw ArithmeticException("Illegal acos(x) for x < -1: x = $x")
        }

        x.value > BigDecimal.ONE -> {
            throw ArithmeticException("Illegal acos(x) for x > 1: x = $x")
        }

        else -> {
            val result = acos(x.value, mc)

            if (isEquals(result, BigDecimal.ZERO, mc)) {
                Number(BigDecimal.ZERO)
            } else {
                Number(result)
            }
        }
    }
}

/**
 * Returns the arctangent of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun atan(x: Number, mc: MathContext) = Number(atan(x.value, mc))

/**
 * Returns the arccotangent of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun acot(x: Number, mc: MathContext) = Number(acot(x.value, mc))


// Hyperbolic functions:

/**
 * Returns the hyperbolic sine of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun sinh(x: Number, mc: MathContext) = Number(sinh(x.value, mc))

/**
 * Returns the hyperbolic cosine of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun cosh(x: Number, mc: MathContext) = Number(cosh(x.value, mc))

/**
 * Returns the hyperbolic tangent of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun tanh(x: Number, mc: MathContext) = Number(tanh(x.value, mc))

/**
 * Returns the hyperbolic cotangent of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value is equals to zero.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun coth(x: Number, mc: MathContext): Number {
    if (x.value.signum() == 0) {
        throw ArithmeticException("Illegal coth(x) for x = 0")
    } else {
        return Number(coth(x.value, mc))
    }
}


// Inverse hyperbolic functions:

/**
 * Returns the hyperbolic arcsine of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun asinh(x: Number, mc: MathContext) = Number(asinh(x.value, mc))

/**
 * Returns the hyperbolic arccosine of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value is less than 1.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun acosh(x: Number, mc: MathContext): Number {
    if (x.value < BigDecimal.ONE) {
        throw ArithmeticException("Illegal acosh(x) for x < 1: x = $x")
    } else {
        return Number(acosh(x.value, mc))
    }
}

/**
 * Returns the hyperbolic arctangent of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value is less than or equal to -1, or
 * if the [x] value is greater than or equal to 1.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun atanh(x: Number, mc: MathContext): Number {
    return when {
        x.value <= BigDecimal.ONE.negate(mc) -> {
            throw ArithmeticException("Illegal atanh(x) for x <= -1: x = $x")
        }

        x.value >= BigDecimal.ONE -> {
            throw ArithmeticException("Illegal atanh(x) for x >= 1: x = $x")
        }

        else -> Number(atanh(x.value, mc))
    }
}

/**
 * Returns the hyperbolic arccotangent of the [x] value, rounding it to
 * the precision specified in the [mc].
 *
 * @throws ArithmeticException If the [x] value belong to [-1, +1].
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun acoth(x: Number, mc: MathContext): Number {
    if (x.value in BigDecimal.ONE.negate(mc)..BigDecimal.ONE) {
        throw ArithmeticException("Illegal acoth(x) for x >= -1 or x <= 1: x = $x")
    } else {
        return Number(acoth(x.value, mc))
    }
}


// Rounding functions:

/**
 * Returns the smallest integer greater than or equal to the [x] value.
 */
fun ceil(x: Number) = Number(x.value.setScale(0, RoundingMode.CEILING))

/**
 * Returns the greater integer less than or equal to the [x] value.
 */
fun floor(x: Number) = Number(x.value.setScale(0, RoundingMode.FLOOR))

/**
 * Returns the nearest integer relative to [x] value (the half-up rounding). Examples of rounding:
 * * 5.5 to 6;
 * * 5.3 to 5;
 * * 5.7 to 6;
 * * -5.5 to -6;
 * * -5.3 to -5;
 * * -5.7 to -6;
 */
fun round(x: Number) = Number(x.value.setScale(0, RoundingMode.HALF_UP))


// Other functions:

/**
 * Returns the absolute value of the [x], rounding it to the precision specified in the [mc].
 */
fun abs(x: Number, mc: MathContext) = Number(x.value.abs(mc))

/**
 * Returns the fractional part of the [x] value.
 */
fun fraction(x: Number) = x.subtract(floor(x), MathContext(0, RoundingMode.HALF_UP))

/**
 * Returns the hypotenuse with the [x] and [y] legs, rounding it
 * to the precision specified in the [mc].
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
fun hypot(x: Number, y: Number, mc: MathContext) = sqrt(
    x.pow(Number(BigDecimal(2)), mc).add(y.pow(Number(BigDecimal(2)), mc), mc), mc)


// Auxiliary functions:

/**
 * Returns the remainder of dividing the [x] value by the double pi number.
 *
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
private fun mod2pi(x: BigDecimal, mc: MathContext): BigDecimal {
    val doublePi = BigDecimal(2).multiply(pi(mc), mc)
    return x.remainder(doublePi, mc)
}

/**
 * Checks whether the values of [x] and [y] are equals.
 * The values of [x] and [y] are equal if the absolute value
 * of the subtraction of [x] and [y] is less than 10^([mc].precision / 2).
 */
private fun isEquals(x: BigDecimal, y: BigDecimal, mc: MathContext): Boolean {
    val epsilon = BigDecimal(10).divide(BigDecimal(10).pow(mc.precision / 2))
    return x.subtract(y, mc).abs(mc) < epsilon
}
