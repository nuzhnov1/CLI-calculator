package calculator

import java.math.MathContext

fun main() {
    val calculator = Calculator(MathContext.DECIMAL128)

    while (true) {
        val input = readLine()

        try {
            when (val result = calculator.executeStatement(input)) {
                null -> Unit
                is Number -> println(result)
                is Command.HELP -> println(Command.HELP)
                is Command.FUNCTIONS -> println(Command.FUNCTIONS)
                is Command.EXIT -> break
            }
        } catch (e: CalculatorException) {
            println(e.localizedMessage)
        }
    }
}
