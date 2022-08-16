package calculator

import calculator.parser.Parser
import calculator.parser.PostfixItem
import calculator.tokenizer.CR
import calculator.tokenizer.LF
import java.io.Reader
import java.io.StringReader
import java.math.BigInteger

/**
 * The main class of the calculator.
 * This class is used to compute expressions read from various reading streams.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Calculator {
    internal sealed interface Operand
    internal data class Ident(val name: String) : Operand


    /**
     * The mutable map of declared variables.
     */
    val declaredVariables = mutableMapOf<String, Number>()

    private val operandStack = ArrayDeque<Operand>()


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
                    PostfixItem.Kind.COMMAND -> return executeCommand(it.lexem)
                }
            }

            return operandStack.popNumberOrNull()
        } catch (e: ArithmeticException) {
            val message = e.localizedMessage

            throw if ("overflow" in message.lowercase()) {
                ExecutionException("Overflow", e)
            } else {
                ExecutionException(e.localizedMessage, e)
            }
        } finally {
            // Reset calculator state:
            operandStack.clear()
        }
    }


    private fun makeNumber(representation: String) = Number(BigInteger(representation))

    private fun derefIdent(name: String) =
        declaredVariables[name] ?: throw ExecutionException("Undeclared variable '$name'")

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

                operandStack.addFirst(first + second)
            }

            "-" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first - second)
            }

            "*" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first * second)
            }

            "/" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first / second)
            }

            "%" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first % second)
            }

            "u-" -> {
                val operand = operandStack.popNumber()
                operandStack.addFirst(-operand)
            }

            "^" -> {
                val second = operandStack.popNumber()
                val first = operandStack.popNumber()

                operandStack.addFirst(first pow second)
            }

            "!" -> {
                val number = operandStack.popNumber()
                operandStack.addFirst(number.factorial())
            }
        }
    }

    private fun declareVariable() {
        val number = operandStack.popNumber()
        val ident = operandStack.popIdent().name

        declaredVariables[ident] = number
    }

    private fun executeCommand(commandName: String) =
        when (commandName) {
            "help" -> Command.HELP
            "exit" -> Command.EXIT
            else -> throw ExecutionException("Unknown command '$commandName'")
        }
}
