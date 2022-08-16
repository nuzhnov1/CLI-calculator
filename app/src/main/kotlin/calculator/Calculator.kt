package calculator

import calculator.parser.Parser
import calculator.parser.PostfixItem
import calculator.tokenizer.CR
import calculator.tokenizer.LF
import ch.obermuhlner.math.big.BigDecimalMath.e
import ch.obermuhlner.math.big.BigDecimalMath.pi
import java.io.Reader
import java.io.StringReader
import java.math.BigDecimal
import java.math.MathContext

/**
 * The main class of the calculator.
 * This class is used to compute expressions read from various reading streams.
 *
 * @param mc the precision of numbers and the rounding mode used in calculations.
 * @throws UnsupportedOperationException If the [mc] has unlimited precision.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Calculator(private val mc: MathContext = MathContext.DECIMAL128) {
    internal sealed interface Operand
    internal data class Ident(val name: String) : Operand


    /**
     * The mutable map of declared variables.
     */
    val declaredVariables = mutableMapOf<String, Number>()

    private val operandStack = ArrayDeque<Operand>()
    private val actualArgumentsStack = ArrayDeque<Number>()
    private val constants: Map<String, Number> = mapOf(
        "pi" to Number(pi(mc)),
        "e" to Number(e(mc))
    )


    /**
     * Reads a single statement from the [input] and execute it.
     * 1. If the [input] is null, the calculator returns [Command.EXIT].
     * 2. If the [input] is an empty string, a string with only whitespaces or
     * a variable declaration, the calculator returns null.
     * 3. If the [input] is an expression, the calculator returns [Number] instance.
     * 4. If the [input] is a command, the calculator returns one of the objects of the [Command] class
     * depending on the command entered.
     *
     * @throws CalculatorException If a syntax or execution error occurs.
     */
    fun executeStatement(input: String?): CalculationResult? {
        // If the input is EOF:
        if (input == null) {
            return executeStatement(StringReader(""))
        }

        return when (input.lastOrNull()) {
            LF, CR -> executeStatement(StringReader(input))
            else -> executeStatement(StringReader(input + LF))
        }
    }

    /**
     * Reads a single statement from the [reader] and execute it.
     * 1. If the [reader] returns EOF, the calculator returns [Command.EXIT].
     * 2. If the [reader] returns an empty string, a string with only whitespaces or
     * a variable declaration, the calculator returns null.
     * 3. If the [reader] returns an expression, the calculator returns [Number] instance.
     * 4. If the [reader] returns a command, the calculator returns one of the objects of the [Command] class
     * depending on the command entered.
     *
     * @throws CalculatorException If a syntax or execution error occurs.
     * @throws java.io.IOException If an I/O error occurs.
     */
    fun executeStatement(reader: Reader): CalculationResult? {
        val postfixItems = Parser().parse(reader)

        try {
            postfixItems.forEach {
                when (it.kind) {
                    PostfixItem.Kind.NUMBER -> operandStack.addFirst(makeNumber(it.lexem))
                    PostfixItem.Kind.IDENT -> operandStack.addFirst(Ident(it.lexem))
                    PostfixItem.Kind.OP -> executeOperator(it.lexem)
                    PostfixItem.Kind.ASSIGN -> declareVariable()
                    PostfixItem.Kind.ACTION -> performAction(it.lexem)
                    PostfixItem.Kind.COMMAND -> return executeCommand(it.lexem)
                }
            }

            return operandStack.popNumberOrNull()
        } catch (e: ArithmeticException) {
            throw ExecutionException(e.localizedMessage, e)
        } finally {
            // Reset calculator state:
            operandStack.clear()
            actualArgumentsStack.clear()
        }
    }


    private fun makeNumber(representation: String) =
        try {
            Number(BigDecimal(representation, mc))
        } catch (e: NumberFormatException) {
            throw ExecutionException("'$representation' could not be converted to a number", e)
        }

    private fun derefIdent(name: String) =
        when {
            constants[name] != null -> constants[name]!!
            declaredVariables[name] != null -> declaredVariables[name]!!
            else -> throw ExecutionException("Undeclared variable '$name'")
        }

    private fun ArrayDeque<Operand>.popNumberOrNull() =
        when (val operand = removeFirstOrNull()) {
            is Number -> operand
            is Ident -> derefIdent(operand.name)
            else -> null
        }

    private fun ArrayDeque<Operand>.popNumber() = popNumberOrNull()!!
    private fun ArrayDeque<Operand>.popIdent() = removeFirst() as Ident

    private fun executeOperator(operator: String) {
        when (operator) {
            "+" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first.add(second, mc))
            }

            "-" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first.subtract(second, mc))
            }

            "*" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first.multiply(second, mc))
            }

            "/" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first.divide(second, mc))
            }

            "u-" -> {
                val operand = operandStack.popNumber()
                operandStack.addFirst(operand.negate(mc))
            }

            "^" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first.pow(second, mc))
            }

            "!" -> {
                val number = operandStack.popNumber()
                operandStack.addFirst(number.factorial(mc))
            }

            "%" -> {
                val number = operandStack.popNumber()
                operandStack.addFirst(number.percent(mc))
            }
        }
    }

    private fun declareVariable() {
        val number = operandStack.popNumber()
        val ident = operandStack.popIdent().name

        if (ident in constants) {
            throw ExecutionException("A constant with name '$ident' is already declared")
        } else {
            declaredVariables[ident] = number
        }
    }

    private fun performAction(actionName: String) {
        when (actionName) {
            "invoke" -> invokeFunction()
            "put_arg" -> actualArgumentsStack.addFirst(operandStack.popNumber())
        }
    }

    private fun invokeFunction() {
        val function = operandStack.popIdent()
        val result = invokeFunction(function.name, actualArgumentsStack, mc)

        operandStack.addFirst(result)
    }

    private fun executeCommand(commandName: String) =
        when (commandName) {
            "help" -> Command.HELP
            "functions" -> Command.FUNCTIONS
            "exit" -> Command.EXIT
            else -> throw ExecutionException("Unknown command '$commandName'")
        }
}
