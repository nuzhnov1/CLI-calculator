package calculator

import calculator.parser.Parser
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.StringReader

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testing methods of the Parser class")
internal class TestParser {
    @BeforeAll
    fun start() {
        println("Testing methods of the Parser class...")
    }

    @Test
    @DisplayName(
        """
        Testing the parsing of the assignment statements
        """
    )
    fun testParseAssignStatements() {
        println("\tTesting the parsing of the assigment statements...")

        print("\t\tTesting the parsing of the valid assigment statements... ")
        testParseStatement("a= \t1.0-5\n\n", "a 1.0 5 - =")
        testParseStatement("a=b\n", "a b =")
        testParseStatement("a =+2\n", "a 2 =")
        testParseStatement("a  =  -2\n", "a 2 u- =")
        testParseStatement("e1 = 0\n", "e1 0 =")
        testParseStatement("a=( b )\n", "a b =")
        println("OK")

        print("\t\tTesting the parsing of the invalid assigment statements... ")
        testParseInvalidStatement("a=1+1", "Expected end of line, got end of file")
        testParseInvalidStatement("a==", "Expected expression, got '='")
        testParseInvalidStatement("a=\n", "Expected expression, got end of line")
        testParseInvalidStatement("a=*", "Expected expression, got '*'")
        testParseInvalidStatement("a=/help", "Expected expression, got '/'")
        println("OK")

        println("\tTesting the parsing of the assigment statements... OK")
    }

    @Test
    @DisplayName(
        """
        Testing the parsing of the command statements
        """
    )
    fun testParseCommandStatements() {
        println("\tTesting the parsing of the command statements...")

        print("\t\tTesting the parsing of the valid command statements... ")
        testParseStatement(" /help  \n", "help")
        println("OK")

        print("\t\tTesting the parsing of the invalid command statement... ")
        testParseInvalidStatement("/go*1", "Expected end of line, got '*'")
        testParseInvalidStatement("/go  1", "Expected end of line, got '1'")
        testParseInvalidStatement("/go,", "Expected end of line, got ','")
        println("OK")

        println("\tTesting the parsing of the command statements... OK")
    }

    @Test
    @DisplayName(
        """
        Testing the parsing of the empty statements
        """
    )
    fun testParseEmptyStatements() {
        print("\tTesting the parsing of the empty statements... ")
        testParseStatement("", "exit")
        testParseStatement(" \n \n\n\n", "")
        testParseStatement("\t", "exit")
        testParseStatement("\t\n", "")
        println("OK")
    }

    @Test
    @DisplayName(
        """
        Testing the parsing of simple expressions, such as: '1 + 1', '2 * 2', etc
        """
    )
    fun testParseSimpleExpressions() {
        println("\tTesting the parsing of simple expressions, such as: '1 + 1', '2 * 2', etc...")

        print("\t\tTesting the parsing of valid simple expressions... ")
        testParseStatement("5\n", "5")
        testParseStatement("e\n", "e")
        testParseStatement("+ 1.0\n", "1.0")
        testParseStatement("-5\n", "5 u-")
        testParseStatement("(1)\n", "1")
        testParseStatement("[1 + 1]\n", "1 1 +")
        testParseStatement("1-1\n", "1 1 -")
        testParseStatement("a + b\n", "a b +")
        testParseStatement("1.0 -  2\n", "1.0 2 -")
        testParseStatement("\t 2.0\t*\t3.2\n", "2.0 3.2 *")
        testParseStatement("1.0/0\n", "1.0 0 /")
        testParseStatement("1.0/a\n", "1.0 a /")
        testParseStatement("24a\n", "24 a *")
        testParseStatement("24 12\n", "24 12 *")
        testParseStatement("(23)(24)\n", "23 24 *")
        testParseStatement("(23)[24]\n", "23 24 *")
        testParseStatement("10!a\n", "10 ! a *")
        testParseStatement("4.1  ^ \t 1.1\n", "4.1 1.1 ^")
        testParseStatement("5!\n", "5 !")
        testParseStatement("54 %\n", "54 %")
        println("OK")

        print("\t\tTesting the parsing of invalid simple expressions... ")
        testParseInvalidStatement("*1", "Expected expression or command, got '*'")
        testParseInvalidStatement(")a", "Expected expression or command, got ')'")
        testParseInvalidStatement(",a", "Expected expression or command, got ','")
        println("OK")

        println("\tTesting the parsing of simple expressions, such as: '1 + 1', '2 * 2', etc... OK")
    }

    @Test
    @DisplayName(
        """
        Testing the association of operators in expressions
        """
    )
    fun testAssociationOfOperators() {
        print("\tTesting the association of operators in expressions... ")
        testParseStatement("9 + 1 - 2\n", "9 1 + 2 -")
        testParseStatement("1.2 * 8 / 2.1\n", "1.2 8 * 2.1 /")
        testParseStatement(" +-+++ 1.2\n", "1.2 u-")
        testParseStatement("2^3^9\n", "2 3 9 ^ ^")
        testParseStatement("2!!%!\n", "2 ! ! % !")
        println("OK")
    }

    @Test
    @DisplayName(
        """
        Testing the operator priorities
        """
    )
    fun testPriorityOfOperators() {
        println("\tTesting the operator priorities...")

        print("\t\tTesting the operator priorities in valid expressions without parenthesis... ")
        testParseStatement("1 + 2 * 3\n", "1 2 3 * +")
        testParseStatement("1 - 2e\n", "1 2 e * -")
        testParseStatement("1 - +-2e\n", "1 2 u- e * -")
        testParseStatement(
            "--+1.0 +-+-+-+ +-1.3\n",
            "1.0 u- u- 1.3 u- u- u- u- +"
        )
        testParseStatement("--1.0 *+ -1.0\n", "1.0 u- u- 1.0 u- *")
        testParseStatement("-7^+-2!\n", "7 2 ! u- ^ u-")
        testParseStatement(
            "-7^+-2!!^-7  \t- +- \t4% * --4var/another_var\n",
            "7 2 ! ! 7 u- ^ u- ^ u- 4 % u- 4 u- u- * var * another_var / -"
        )
        println("OK")

        print("\t\tTesting the operator priorities in invalid expressions without parenthesis... ")
        testParseInvalidStatement("-7+\n", "Expected expression, got end of line")
        testParseInvalidStatement("abc/%\n", "Expected expression, got '%'")
        testParseInvalidStatement("+/abc\n", "Expected expression, got '/'")
        testParseInvalidStatement("-7^*2", "Expected expression, got '*'")
        testParseInvalidStatement("!", "Expected expression or command, got '!'")
        println("OK")

        print("\t\tTesting the operator priorities in valid expressions with parenthesis... ")
        testParseStatement("(1 + 2) * 3\n", "1 2 + 3 *")
        testParseStatement("(1 - 2)e\n", "1 2 - e *")
        testParseStatement(
            "([(-7)^(+2)!!]^-7 - +-4%) * (--4var/another_var)\n",
            "7 u- 2 ! ! ^ 7 u- ^ 4 % u- - 4 u- u- var * another_var / *"
        )
        println("OK")

        print("\t\tTesting the operator priorities in invalid expressions with parenthesis... ")
        testParseInvalidStatement("()\n", "Expected expression, got ')'")
        testParseInvalidStatement("(1", "Expected ')', got end of file")
        testParseInvalidStatement("(1 + 2 * 3", "Expected ')', got end of file")
        testParseInvalidStatement("[2\n", "Expected ']', got end of line")
        println("OK")

        println("\tTesting the operator priorities... OK")
    }

    @Test
    @DisplayName(
        """
        Testing the function calls
        """
    )
    fun testFunctionCalls() {
        println("\tTesting the function calls...")

        print("\t\tTesting the function calls in valid expressions... ")
        testParseStatement("procedure()\n", "procedure invoke")
        testParseStatement("function() + 1\n", "function invoke 1 +")
        testParseStatement("function()2\n", "function invoke 2 *")
        testParseStatement("sin(0)\n", "sin 0 put_arg invoke")
        testParseStatement(
            "sin(0) + function()2\n",
            "sin 0 put_arg invoke function invoke 2 * +"
        )
        testParseStatement("log(2, 1)\n", "log 2 put_arg 1 put_arg invoke")
        testParseStatement(
            "log(5, 1)7 + 5function(1, 2, 3, 4)\n",
            "log 5 put_arg 1 put_arg invoke 7 * 5 function 1 put_arg 2 put_arg 3 put_arg 4 put_arg invoke * +"
        )
        testParseStatement(
            "log(27^ 2, 8!)8 + function(1 + 2)\n",
            "log 27 2 ^ put_arg 8 ! put_arg invoke 8 * function 1 2 + put_arg invoke +"
        )
        testParseStatement(
            "log((2 + 3)1, 2)a + function(((1 + 2))(2 - 3))5\n",
            "log 2 3 + 1 * put_arg 2 put_arg invoke a * function 1 2 + 2 3 - * put_arg invoke 5 * +"
        )
        println("OK")

        print("\t\tTesting the function calls in invalid expressions... ")
        testParseInvalidStatement("log(2 + 3", "Expected ')', got end of file")
        testParseInvalidStatement("log(2 + 3, 1\n", "Expected ')', got end of line")
        println("OK")

        println("\tTesting the function calls... OK")
    }

    @Test
    @Disabled
    @DisplayName(
        """
        Crash test: processing a set of nested expressions
        """
    )
    fun testMultipleNestedExpressions() {
        print("\tCrash test: processing a set of nested expressions... ")

        val nestingDepth = 2000
        val expression = "(".repeat(nestingDepth) + "1" + ")".repeat(nestingDepth) + '\n'

        testParseStatement(expression, "1")

        println("OK")
    }

    @AfterAll
    fun finish() {
        println("Testing methods of the Parser class... OK")
        println()
    }


    private fun testParseStatement(inputString: String, expectedPostfixRecord: String) {
        val parser = Parser()

        val actualPostfixRecord = parser
            .parse(StringReader(inputString))
            .joinToString(separator = " ") { it.lexem }

        assertEquals(
            expectedPostfixRecord, actualPostfixRecord,
            "The actual postfix record is not equal to the expected one"
        )
    }

    private fun testParseInvalidStatement(inputString: String, expectedMessage: String) {
        val parser = Parser()

        val exception = assertThrows<SyntaxException>(expectedMessage) { parser.parse(StringReader(inputString)) }

        assertEquals(
            expectedMessage, exception.message,
            "The actual exception message is not equal to the expected one"
        )
    }
}
