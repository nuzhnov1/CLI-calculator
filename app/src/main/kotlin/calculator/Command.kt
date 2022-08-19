package calculator

/**
 * Sealed interface of calculator commands:
 * 1. Help command;
 * 2. Exit command;
 */
sealed interface Command : CalculationResult {
    object HELP : Command {
        override fun toString() =
            """
            |The simple integer calculator.
            |Supported operators:
            |   1) Addition (+) and subtraction (-);
            |   2) Multiplication (*), division (/) and modulo operation (%).
            |   3) Unary plus (+) and unary minus (-);
            |   4) Exponentiation (^);
            |   5) Factorial (!);
            |Parentheses and square brackets can be used to change the order of operations in expressions.
            |You can declare variables using the following syntax:
            |   <identifier> = <expression>
            |Identifiers are case-sensitive.
            |To exit the program, run the /exit command or press CTRL-D keystroke.
            """.trimMargin().trimIndent()
    }

    object EXIT : Command {
        override fun toString() = "exit"
    }
}
