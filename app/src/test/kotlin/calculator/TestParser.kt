package calculator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.StringReader

@DisplayName("Testing methods of the Parser class")
internal class TestParser {
    @Test
    @DisplayName(
        """
        Testing the parsing of the assignment statements.
        """
    )
    fun testParseAssignStatements() {
        testParseStatement("a= \t1.0-5\n\n", "a 1.0 5 - =")
        testParseStatement("a=b", "a b =")
        testParseStatement("a =+2\n", "a 2 u+ =")
        testParseStatement("a  =  -2", "a 2 u- =")
        testParseStatement("e1 = 0", "e1 0 =")
        testParseStatement("a=( b )", "a b =")

        testParseInvalidStatement("a==", "expected expression, got '='")
        testParseInvalidStatement("a=\n", "expected expression, got end of line")
        testParseInvalidStatement("a=", "expected expression, got end of line")
        testParseInvalidStatement("a=*", "expected expression, got '*'")
        testParseInvalidStatement("a=/help", "expected expression, got '/'")
    }

    @Test
    @DisplayName(
        """
        Testing the parsing of the command statements.
        """
    )
    fun testParseCommandStatements() {
        testParseStatement(" /help  ", "help")

        testParseInvalidStatement("/go*1", "expected end of line, got '*'")
        testParseInvalidStatement("/go  1", "expected end of line, got '1'")
        testParseInvalidStatement("/go,", "expected end of line, got ','")
    }

    @Test
    @DisplayName(
        """
        Testing the parsing of the empty statements.
        """
    )
    fun testParseEmptyStatements() {
        testParseStatement("", "")
        testParseStatement(" \n \n\n\n", "")
        testParseStatement("\t", "")
    }

    @Test
    @DisplayName(
        """
        Testing parsing of simple expressions, such as: "1 + 1", "2 * 2", etc.
        """
    )
    fun testParseSimpleExpressions() {
        testParseStatement("1+1", "1 1 +")
        testParseStatement("a + b\n", "a b +")
        testParseStatement("1.0 -  2", "1.0 2 -")
        testParseStatement("\t 2.0\t*\t3.2", "2.0 3.2 *")
        testParseStatement("1.0/0", "1.0 0 /")
        testParseStatement("1.0/a", "1.0 a /")
        testParseStatement("24a", "24 a *")
        testParseStatement("+ 1.0", "1.0 u+")
        testParseStatement("-5", "5 u-")
        testParseStatement("4.1  ^ \t 1.1", "4.1 1.1 ^")
        testParseStatement("5!", "5 !")
        testParseStatement("54 %", "54 %")
        testParseStatement("5", "5")
        testParseStatement("\t 1.2\t", "1.2")
        testParseStatement("(1)", "1")

        testParseInvalidStatement("*1", "expected expression or command, got '*'")
        testParseInvalidStatement(")a", "expected expression or command, got ')'")
        testParseInvalidStatement(",a", "expected expression or command, got ','")
    }

    @Test
    @DisplayName(
        """
        Testing the association of operators in expressions.
        """
    )
    fun testAssociationOfOperators() {
        testParseStatement("9 + 1 - 2", "9 1 + 2 -")
        testParseStatement("1.2 * 8 / 2.1", "1.2 8 * 2.1 /")
        testParseStatement(" +-+++ 1.2", "1.2 u+ u+ u+ u- u+")
        testParseStatement("2^3^9", "2 3 9 ^ ^")
        testParseStatement("2!!%!", "2 ! ! % !")
    }

    @Test
    @DisplayName(
        """
        Testing the operator priorities.
        """
    )
    fun testPriorityOfOperators() {
        testParseStatement("1 + 2 * 3", "1 2 3 * +")
        testParseStatement("1 - 2e", "1 2 e * -")
        testParseStatement("1 - +-2e", "1 2 u- u+ e * -")
        testParseStatement(
            "--+1.0 +-+-+-+ +-1.3",
            "1.0 u+ u- u- 1.3 u- u+ u+ u- u+ u- u+ u- +"
        )
        testParseStatement("--1.0 *+ -1.0", "1.0 u- u- 1.0 u- u+ *")
        testParseStatement("-7^+-2!", "7 2 ! u- u+ ^ u-")
        testParseStatement(
            "-7^+-2!!^-7  \t- +- \t4% * --4var/another_var",
            "7 2 ! ! 7 u- ^ u- u+ ^ u- 4 % u- u+ 4 u- u- * var * another_var / -"
        )

        testParseInvalidStatement("-7+", "expected expression, got end of line")
        testParseInvalidStatement("abc/%", "expected expression, got '%'")
        testParseInvalidStatement("+/abc", "expected expression, got '/'")
        testParseInvalidStatement("-7^*2", "expected expression, got '*'")
        testParseInvalidStatement("!", "expected expression or command, got '!'")

        testParseStatement("(1 + 2) * 3", "1 2 + 3 *")
        testParseStatement("(1 - 2)e", "1 2 - e *")
        testParseStatement(
            "(((-7)^(+2)!!)^-7 - +-4%) * (--4var/another_var)",
            "7 u- 2 u+ ! ! ^ 7 u- ^ 4 % u- u+ - 4 u- u- var * another_var / *"
        )

        testParseInvalidStatement("()", "expected expression, got ')'")
        testParseInvalidStatement("(1", "expected ')', got end of line")
        testParseInvalidStatement("(1 + 2 * 3", "expected ')', got end of line")
    }

    @Test
    @DisplayName(
        """
        Testing the function calls.
        """
    )
    fun testFunctionCalls() {
        testParseStatement("procedure()", "procedure invoke")
        testParseStatement("function() + 1", "function invoke 1 +")
        testParseStatement("function()2", "function invoke 2 *")
        testParseStatement("sin(0)", "sin 0 put_arg invoke")
        testParseStatement(
            "sin(0) + function()2",
            "sin 0 put_arg invoke function invoke 2 * +"
        )
        testParseStatement("log(2, 1)", "log 2 put_arg 1 put_arg invoke")
        testParseStatement(
            "log(5, 1)7 + 5function(1, 2, 3, 4)",
            "log 5 put_arg 1 put_arg invoke 7 * 5 function 1 put_arg 2 put_arg 3 put_arg 4 put_arg invoke * +"
        )
        testParseStatement(
            "log(27^ 2, 8!)8 + function(1 + 2)",
            "log 27 2 ^ put_arg 8 ! put_arg invoke 8 * function 1 2 + put_arg invoke +"
        )
        testParseStatement(
            "log((2 + 3)1, 2)a + function(((1 + 2))(2 - 3))5",
            "log 2 3 + 1 * put_arg 2 put_arg invoke a * function 1 2 + 2 3 - * put_arg invoke 5 * +"
        )

        testParseInvalidStatement("log(2 + 3", "expected ')', got end of line")
        testParseInvalidStatement("log(2 + 3, 1", "expected ')', got end of line")
    }

    @Test
    @DisplayName(
        """
        Crash test: processing a set of nested expressions.
        """
    )
    fun testMultipleNestedExpressions() {
        val nestingDepth = 2000
        val expression = "(".repeat(nestingDepth) + "1" + ")".repeat(nestingDepth)

        testParseStatement(expression, "1")
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
