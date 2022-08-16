@file:Suppress("unused")

package calculator

/**
 * The base exceptions class in this package.
 */
sealed class CalculatorException : RuntimeException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(cause: Throwable): super(cause)
    constructor(message: String, cause: Throwable): super(message, cause)
}

/**
 * Grammar-related exceptions.
 * This exception is thrown, for example, when illegal statements or lexical errors were occurred.
 */
class SyntaxException : CalculatorException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(cause: Throwable): super(cause)
    constructor(message: String, cause: Throwable): super(message, cause)
}

/**
 * Runtime calculator exceptions.
 * This exception is thrown, for example, when there
 * is an attempt to access an unknown variable or command.
 */
class ExecutionException : CalculatorException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(cause: Throwable): super(cause)
    constructor(message: String, cause: Throwable): super(message, cause)
}
