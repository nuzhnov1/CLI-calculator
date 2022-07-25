package calculator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.StringReader

@DisplayName("Testing methods of the TokenStream class")
class TestTokenStream {
    private fun testSingleToken(inputString: String, expectedToken: Token) {
        val tokenStream = TokenStream(CharStream(StringReader(inputString)))
        val token = tokenStream.next()

        tokenStream.use {
            assertEquals(expectedToken, token, "Actual token are not equal to expected one")
        }
    }

    private fun testMultipleTokens(inputString: String, expectedTokens: List<Token>) {
        val tokenStream = TokenStream(CharStream(StringReader(inputString)))
        val tokens = mutableListOf<Token>()

        tokenStream.forEach { tokens.add(it) }

        tokenStream.use {
            assertEquals(
                expectedTokens.count(), tokens.count(),
                "Actual count of tokens is not equal to expected one"
            )

            expectedTokens.zip(tokens).forEach {
                assertEquals(
                    it.first, it.second,
                    "Actual token are not equal to expected one"
                )
            }
        }
    }

    private fun testInvalidTokens(inputString: String, expectedMessage: String) {
        val tokenStream = TokenStream(CharStream(StringReader(inputString)))

        tokenStream.use {
            assertThrows<SyntaxException>(expectedMessage) { tokenStream.forEach { _ -> } }
        }
    }


    @Test
    @DisplayName(
        """
        Testing the zero state of a FSM (method next).
        """
    )
    fun testState0() {
        testSingleToken("", Token(TokenType.EOF, ""))
        testSingleToken("\n", Token(TokenType.EOL, "\n"))
        testSingleToken("$CR", Token(TokenType.EOL, "\n"))
        testSingleToken("*", Token(TokenType.OP, "*"))
        testSingleToken(",", Token(TokenType.COLON, ","))
        testSingleToken("=", Token(TokenType.ASSIGN, "="))
        testSingleToken("(", Token(TokenType.PARENTHESES, "("))

        testInvalidTokens("\n\n@", "Syntax error: illegal character '@' on 1 position")
    }

    @Test
    @DisplayName(
        """
        Testing the 1st state of a FSM (method state1).
        Testing reading of integer numbers.
        """
    )
    fun testState1() {
        testSingleToken("0110", Token(TokenType.INT, "0110"))
        testMultipleTokens("0123456789$CR\n", listOf(
            Token(TokenType.INT, "0123456789"),
            Token(TokenType.EOL, "\n")
        ))
        testMultipleTokens("123a", listOf(
            Token(TokenType.INT, "123"),
            Token(TokenType.OP, "*"),
            Token(TokenType.IDENT, "a")
        ))

        testInvalidTokens("123@", "Syntax error: illegal character '@' on 4 position")
    }

    @Test
    @DisplayName(
        """
        Testing the 2nd state of a FSM (method state2).
        """
    )
    fun testState2() {
        testInvalidTokens(".@", "Syntax error: illegal character '.' on 1 position")
    }

    @Test
    @DisplayName(
        """
        Testing the 3rd state of a FSM (method state3).
        Testing reading of float numbers.
        """
    )
    fun testState3() {
        testSingleToken("1.1", Token(TokenType.FLOAT, "1.1"))

        testMultipleTokens(".1+", listOf(
            Token(TokenType.FLOAT, ".1"),
            Token(TokenType.OP, "+")
        ))
        testMultipleTokens("1.e", listOf(
            Token(TokenType.FLOAT, "1."),
            Token(TokenType.OP, "*"),
            Token(TokenType.IDENT, "e")
        ))

        testInvalidTokens("1.@", "Syntax error: illegal character '@' on 3 position")
    }

    @Test
    @DisplayName(
        """
        Testing the 4th state of FSM (method state4).
        Testing reading identifiers.
        """
    )
    fun testState4() {
        testSingleToken("_1", Token(TokenType.IDENT, "_1"))
        testSingleToken("val", Token(TokenType.IDENT, "val"))
        testSingleToken("e1", Token(TokenType.IDENT, "e1"))

        testMultipleTokens("abe1+", listOf(
            Token(TokenType.IDENT, "abe1"),
            Token(TokenType.OP, "+")
        ))
        testMultipleTokens("a1 \t\t\t \t\t \t", listOf(
            Token(TokenType.IDENT, "a1"),
            Token(TokenType.SPACES, " \t\t\t \t\t \t"),
        ))

        testInvalidTokens("e1.", "Syntax error: illegal character '.' on 3 position")
    }

    @Test
    @DisplayName(
        """
        Testing the 5th state of FSM (method state5).
        """
    )
    fun testState5() {
        testSingleToken("/", Token(TokenType.OP, "/"))

        testMultipleTokens("/1", listOf(
            Token(TokenType.OP, "/"),
            Token(TokenType.INT, "1"),
        ))
        testMultipleTokens("/ \t", listOf(
            Token(TokenType.OP, "/"),
            Token(TokenType.SPACES, " \t"),
        ))

        testInvalidTokens("/.", "Syntax error: illegal character '.' on 2 position")
    }

    @Test
    @DisplayName(
        """
        Testing the 6th state of FSM (method state6).
        Testing reading commands.
        """
    )
    fun testState6() {
        testSingleToken("/exit", Token(TokenType.COMMAND, "/exit"))
        testSingleToken("/help", Token(TokenType.COMMAND, "/help"))
        testSingleToken("/help1", Token(TokenType.COMMAND, "/help1"))

        testMultipleTokens("/exit+", listOf(
            Token(TokenType.COMMAND, "/exit"),
            Token(TokenType.OP, "+"),
        ))

        testInvalidTokens("/exit#", "Syntax error: illegal character '#' on 6 position")
    }

    @Test
    @DisplayName(
        """
        Testing the 7th state of FSM (method state7).
        Testing reading spaces.
        """
    )
    fun testState7() {
        testSingleToken("   ", Token(TokenType.SPACES, "   "))

        testMultipleTokens(" /", listOf(
            Token(TokenType.SPACES, " "),
            Token(TokenType.OP, "/"),
        ))

        testInvalidTokens("   $", "Syntax error: illegal character '$' on 4 position")
    }


    @Test
    @DisplayName(
        """
        Testing reading expression: "+-+1  / -2 * 3e1\n"
        """
    )
    fun testExpression1() {
        testMultipleTokens("+-+1  / -2 * 3e1\n", listOf(
            Token(TokenType.OP, "+"),
            Token(TokenType.OP, "-"),
            Token(TokenType.OP, "+"),
            Token(TokenType.INT, "1"),
            Token(TokenType.SPACES, "  "),
            Token(TokenType.OP, "/"),
            Token(TokenType.SPACES, " "),
            Token(TokenType.OP, "-"),
            Token(TokenType.INT, "2"),
            Token(TokenType.SPACES, " "),
            Token(TokenType.OP, "*"),
            Token(TokenType.SPACES, " "),
            Token(TokenType.INT, "3"),
            Token(TokenType.OP, "*"),
            Token(TokenType.IDENT, "e1"),
            Token(TokenType.EOL, "\n")
        ))
    }

    @Test
    @DisplayName(
        """
        Testing reading expression: "+-+1  /exit \t -2 / 3E-a1b\n"
        """
    )
    fun testExpression2() {
        testMultipleTokens("+-+1  /exit \t -2 / 3E-a1b\n", listOf(
            Token(TokenType.OP, "+"),
            Token(TokenType.OP, "-"),
            Token(TokenType.OP, "+"),
            Token(TokenType.INT, "1"),
            Token(TokenType.SPACES, "  "),
            Token(TokenType.COMMAND, "/exit"),
            Token(TokenType.SPACES, " \t "),
            Token(TokenType.OP, "-"),
            Token(TokenType.INT, "2"),
            Token(TokenType.SPACES, " "),
            Token(TokenType.OP, "/"),
            Token(TokenType.SPACES, " "),
            Token(TokenType.INT, "3"),
            Token(TokenType.OP, "*"),
            Token(TokenType.IDENT, "E"),
            Token(TokenType.OP, "-"),
            Token(TokenType.IDENT, "a1b"),
            Token(TokenType.EOL, "\n"),
        ))
    }

    @Test
    @DisplayName(
        """
        Testing reading expression: "+--+1 ++++ ---3e1 +-+ 3eA"
        """
    )
    fun testExpression3() {
        testMultipleTokens("+--+1 ++++ ---3e1 +-+ 3eA", listOf(
            Token(TokenType.OP, "+"),
            Token(TokenType.OP, "-"),
            Token(TokenType.OP, "-"),
            Token(TokenType.OP, "+"),
            Token(TokenType.INT, "1"),
            Token(TokenType.SPACES, " "),
            Token(TokenType.OP, "+"),
            Token(TokenType.OP, "+"),
            Token(TokenType.OP, "+"),
            Token(TokenType.OP, "+"),
            Token(TokenType.SPACES, " "),
            Token(TokenType.OP, "-"),
            Token(TokenType.OP, "-"),
            Token(TokenType.OP, "-"),
            Token(TokenType.INT, "3"),
            Token(TokenType.OP, "*"),
            Token(TokenType.IDENT, "e1"),
            Token(TokenType.SPACES, " "),
            Token(TokenType.OP, "+"),
            Token(TokenType.OP, "-"),
            Token(TokenType.OP, "+"),
            Token(TokenType.SPACES, " "),
            Token(TokenType.INT, "3"),
            Token(TokenType.OP, "*"),
            Token(TokenType.IDENT, "eA"),
        ))
    }


    @Test
    @Disabled
    @DisplayName(
        """
        Testing reading a long identifier.
        """
    )
    fun testLongIdentifier() {
        val longIdentifier = "a".repeat(10000)
        testSingleToken(longIdentifier, Token(TokenType.IDENT, longIdentifier))
    }
}
