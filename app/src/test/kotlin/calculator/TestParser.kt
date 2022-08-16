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
        testParseStatement("a= \t10-5\n\n", "a 10 5 - =")
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
        testParseStatement("+ 10\n", "10")
        testParseStatement("-5\n", "5 u-")
        testParseStatement("(1)\n", "1")
        testParseStatement("[1 + 1]\n", "1 1 +")
        testParseStatement("1-1\n", "1 1 -")
        testParseStatement("a + b\n", "a b +")
        testParseStatement("10 -  2\n", "10 2 -")
        testParseStatement("\t 20\t*\t32\n", "20 32 *")
        testParseStatement("10/0\n", "10 0 /")
        testParseStatement("10/a\n", "10 a /")
        testParseStatement("24a\n", "24 a *")
        testParseStatement("24 12\n", "24 12 *")
        testParseStatement("(23)(24)\n", "23 24 *")
        testParseStatement("(23)[24]\n", "23 24 *")
        testParseStatement("10!a\n", "10 ! a *")
        testParseStatement("41  ^ \t 11\n", "41 11 ^")
        testParseStatement("5!\n", "5 !")
        println("OK")

        print("\t\tTesting the parsing of invalid simple expressions... ")
        testParseInvalidStatement("*1", "Expected expression or command, got '*'")
        testParseInvalidStatement(")a", "Expected expression or command, got ')'")
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
        testParseStatement("12 * 8 / 21\n", "12 8 * 21 /")
        testParseStatement(" +-+++ 12\n", "12 u-")
        testParseStatement("2^3^9\n", "2 3 9 ^ ^")
        testParseStatement("2!!%2\n", "2 ! ! 2 %")
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
            "--+10 +-+-+-+ +-13\n",
            "10 u- u- 13 u- u- u- u- +"
        )
        testParseStatement("--10 %+ -10\n", "10 u- u- 10 u- %")
        testParseStatement("-7^+-2!\n", "7 2 ! u- ^ u-")
        testParseStatement(
            "-7^+-2!!^-7  \t- +- \t4 * --4var/another_var\n",
            "7 2 ! ! 7 u- ^ u- ^ u- 4 u- 4 u- u- * var * another_var / -"
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
            "([(-7)^(+2)!!]^-7 - +-4) * (--4var/another_var)\n",
            "7 u- 2 ! ! ^ 7 u- ^ 4 u- - 4 u- u- var * another_var / *"
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
