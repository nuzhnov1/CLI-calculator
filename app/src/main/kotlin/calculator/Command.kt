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
            |The calculator also supports an implicit multiplication operator, for example, the expression "1a"
            |is equivalent to "1*a", but "a1" is not equivalent to "a*1", in this case "a1" is the only identifier.
            |Parentheses and square brackets can be used to change the order of operations in expressions.
            |You can declare variables using the following syntax:
            |   <identifier> = <expression>
            |Identifiers are case-sensitive.
            """.trimMargin().trimIndent()
    }

    object EXIT : Command {
        override fun toString() = "exit"
    }
}
