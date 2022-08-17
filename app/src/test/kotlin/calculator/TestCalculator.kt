package calculator

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName(
    """
    Testing methods of the Calculator and Number classes
    """
)
internal class TestCalculator {
    private val calculator = Calculator()


    @BeforeAll
    fun start() {
        println("Testing methods of the Calculator and Number classes...")
    }

    @Test
    @DisplayName(
        """
        Testing the outputs of numbers
        """
    )
    fun testOutputOfNumbers() {
        print("\tTesting the outputs of numbers... ")
        testExecutionStatement("123456789", "123456789")
        testExecutionStatement("10^9", "1000000000")
        testExecutionStatement("10^10", "10000000000")
        testExecutionStatement("111111111111111", "111111111111111")
        println("OK")
    }

    @Test
    @DisplayName(
        """
        Testing the execution of operators
        """
    )
    fun testOperators() {
        println("\tTesting the execution of operators...")

        print("\t\tTesting the execution of the unary plus operator... ")
        testExecutionStatement("+1", "1")
        testExecutionStatement("+0", "0")
        println("OK")

        print("\t\tTesting the execution of the unary minus operator... ")
        testExecutionStatement("-12", "-12")
        testExecutionStatement("-0", "0")
        println("OK")

        print("\t\tTesting the execution of the addition operator... ")
        testExecutionStatement("1 + 1", "2")
        testExecutionStatement("1 + 0", "1")
        testExecutionStatement("0 + 0", "0")
        println("OK")

        print("\t\tTesting the execution of the subtraction operator... ")
        testExecutionStatement("121 - 69", "52")
        testExecutionStatement("121 - 0", "121")
        testExecutionStatement("0 - 0", "0")
        println("OK")

        print("\t\tTesting the execution of the multiplication operator... ")
        testExecutionStatement("6 * 2", "12")
        testExecutionStatement("0 * 100", "0")
        testExecutionStatement("0 * 0", "0")
        testExecutionStatement("1 * 100000124", "100000124")
        testExecutionStatement("1 * 1", "1")
        println("OK")

        print("\t\tTesting the execution of the division operator... ")
        testExecutionStatement("6 / 3", "2")
        testExecutionStatement("12 / 3", "4")
        testExecutionStatement("12 / 4", "3")
        testExecutionStatement("1 / 5", "0")
        testExecutionStatement("4 / 3", "1")
        testExecutionStatement("0 / 1", "0")
        testExecutionStatement("1 / 0", "Division by zero")
        testExecutionStatement("0 / 0", "Division undefined")
        println("OK")

        print("\t\tTesting the execution of the modulus operator... ")
        testExecutionStatement("6 % 3", "0")
        testExecutionStatement("12 % 5", "2")
        testExecutionStatement("1 % 5", "1")
        testExecutionStatement("3 % 5", "3")
        testExecutionStatement("0 % 1", "0")
        testExecutionStatement("1 % 0", "Division by zero")
        testExecutionStatement("0 % 0", "Division undefined")
        println("OK")

        print("\t\tTesting the execution of the exponentiation operator... ")
        testExecutionStatement("(-2)^-2", "Illegal x^y for y < 0: y = -2")
        testExecutionStatement("(-2)^0", "1")
        testExecutionStatement("(-2)^2", "4")
        testExecutionStatement("(-2)^3", "-8")
        testExecutionStatement("0^-1", "Illegal x^y for y < 0: y = -1")
        testExecutionStatement("0^0", "1")
        testExecutionStatement("0 ^ 1000", "0")
        testExecutionStatement("1^1000", "1")
        testExecutionStatement("2^-2", "Illegal x^y for y < 0: y = -2")
        testExecutionStatement("2^0", "1")
        testExecutionStatement("2^2", "4")
        testExecutionStatement(
            "2^10000000000000",
            "Illegal x^y for y > ${Int.MAX_VALUE}: y = 10000000000000"
        )
        testExecutionStatement("1000000000^1000000000", "Overflow")
        println("OK")

        print("\t\tTesting the execution of the factorial operator... ")
        testExecutionStatement(
            "(-1000000000000000)!",
            "Illegal x! for x < 0: x = -1000000000000000"
        )
        testExecutionStatement("(-1)!", "Illegal x! for x < 0: x = -1")
        testExecutionStatement("0!", "1")
        testExecutionStatement("1!", "1")
        testExecutionStatement("2!", "2")
        testExecutionStatement("10!", "3628800")
        println("OK")

        println("\tTesting the execution of operators... OK")
    }

    @Test
    @DisplayName(
        """
        Testing methods of the Calculator class
        """
    )
    fun testCalculatorMethods() {
        println("\tTesting methods of the Calculator class...")

        calculator.declaredVariables.clear()

        print("\t\tTesting the execution of null-statement... ")
        testExecutionStatement(null, "exit")
        println("OK")

        print("\t\tTesting the execution of empty statements... ")
        testExecutionStatement("", "")
        testExecutionStatement("$LF", "")
        testExecutionStatement("$CR", "")
        testExecutionStatement("$CR$LF", "")
        println("OK")

        print("\t\tTesting the declaring a variable... ")
        testExecutionStatement("a = 1 * 0", "")
        println("OK")

        print("\t\tTesting the using of the declared variable... ")
        testExecutionStatement("a + 1", "1")
        testExecutionStatement("a = 2", "")
        testExecutionStatement("a + 4", "6")
        println("OK")

        print("\t\tTesting an attempt to access an undeclared variable... ")
        testExecutionStatement("b + 22", "Unknown variable")
        println("OK")

        print("\t\tTesting the execution of commands... ")
        testExecutionStatement("/help", Command.HELP.toString())
        testExecutionStatement("/exit", Command.EXIT.toString())
        println("OK")

        print("\t\tTesting the execution of a unknown command... ")
        testExecutionStatement("/go", "Unknown command")
        println("OK")

        calculator.declaredVariables.clear()

        println("\tTesting methods of the Calculator class... OK")
    }

    @Test
    @DisplayName(
        """
        Testing the execution of complex expressions
        """
    )
    fun testComplexExpressions() {
        println("\tTesting the execution of complex expressions... ")

        calculator.declaredVariables.clear()

        testExecutionStatement("9 + 1 - 2\n", "8")
        testExecutionStatement("12 * 8 % 21\n", "12")
        testExecutionStatement(" +-+++ 1", "-1")
        testExecutionStatement("--+10 +-+-+-+ +-13\n", "23")
        testExecutionStatement("var = --10 *+ -10", "")
        testExecutionStatement("another = -7^+2!", "")
        testExecutionStatement(
            "-7^+2!!^3  \t- +- \t4 * --4*var/another\n",
            "-5764769"
        )
        testExecutionStatement("(1 + 2) * 3\n", "9")
        testExecutionStatement(
            "(((-7)^(+2)!!)^2 % +-4) * (--4*var/another)\n",
            "8"
        )

        calculator.declaredVariables.clear()

        println("\tTesting the execution of complex expressions... OK")
    }

    @AfterAll
    fun finish() {
        println("Testing methods of the Calculator and Number classes... OK")
        println()
    }


    private fun testExecutionStatement(inputString: String?, expectedResult: String) {
        val actualResult = try {
            when (val result = calculator.executeStatement(inputString)) {
                null -> ""
                else -> result.toString()
            }
        } catch (e: CalculatorException) {
            e.localizedMessage
        }

        assertEquals(
            expectedResult, actualResult,
            "The actual result is not equal to the expected one"
        )
    }
}
