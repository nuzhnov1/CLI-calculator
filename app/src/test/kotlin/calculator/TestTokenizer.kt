package calculator

import calculator.tokenizer.CR
import calculator.tokenizer.Token
import calculator.tokenizer.Tokenizer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.StringReader

@DisplayName("Testing methods of the Tokenizer class")
internal class TestTokenizer {
    @Test
    @DisplayName(
        """
        Testing of the zero state of a finite state machine (the "next" method).
        """
    )
    fun testState0() {
        testReadSingleToken("", Token(Token.Kind.EOF, ""))
        testReadSingleToken("\n", Token(Token.Kind.EOL, "\n"))
        testReadSingleToken("$CR", Token(Token.Kind.EOL, "\n"))
        testReadSingleToken("*", Token(Token.Kind.OP, "*"))
        testReadSingleToken(",", Token(Token.Kind.COMMA, ","))
        testReadSingleToken("=", Token(Token.Kind.ASSIGN, "="))
        testReadSingleToken("(", Token(Token.Kind.PARENTHESES, "("))

        testReadInvalidTokens("\n\n@", "illegal character '@'")
    }

    @Test
    @DisplayName(
        """
        Testing of the 1st state of a finite state machine (the "state1" method).
        Testing reading integer numbers.
        """
    )
    fun testState1() {
        testReadSingleToken("0110", Token(Token.Kind.INT, "0110"))
        testReadMultipleTokens("0123456789$CR\n", listOf(
            Token(Token.Kind.INT, "0123456789"),
            Token(Token.Kind.EOL, "\n")
        ))
        testReadMultipleTokens("123a", listOf(
            Token(Token.Kind.INT, "123"),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.IDENT, "a")
        ))

        testReadInvalidTokens("123@", "illegal character '@'")
    }

    @Test
    @DisplayName(
        """
        Testing of the 2nd state of a finite state machine (the "state2" method).
        """
    )
    fun testState2() {
        testReadInvalidTokens(".@", "illegal character '.'")
    }

    @Test
    @DisplayName(
        """
        Testing of the 3rd state of a finite state machine (the "state3" method).
        Testing reading float numbers.
        """
    )
    fun testState3() {
        testReadSingleToken("1.1", Token(Token.Kind.FLOAT, "1.1"))

        testReadMultipleTokens(".1+", listOf(
            Token(Token.Kind.FLOAT, ".1"),
            Token(Token.Kind.OP, "+")
        ))
        testReadMultipleTokens("1.e", listOf(
            Token(Token.Kind.FLOAT, "1."),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.IDENT, "e")
        ))

        testReadInvalidTokens("1.@", "illegal character '@'")
    }

    @Test
    @DisplayName(
        """
        Testing of the 4th state of a finite state machine (the "state4" method).
        Testing reading identifiers.
        """
    )
    fun testState4() {
        testReadSingleToken("_1", Token(Token.Kind.IDENT, "_1"))
        testReadSingleToken("val", Token(Token.Kind.IDENT, "val"))
        testReadSingleToken("e1", Token(Token.Kind.IDENT, "e1"))

        testReadMultipleTokens("abe1+", listOf(
            Token(Token.Kind.IDENT, "abe1"),
            Token(Token.Kind.OP, "+")
        ))
        testReadMultipleTokens("a1 \t\t\t \t\t \t", listOf(
            Token(Token.Kind.IDENT, "a1"),
            Token(Token.Kind.SPACES, " \t\t\t \t\t \t"),
        ))

        testReadInvalidTokens("e1.", "illegal character '.'")
    }

    @Test
    @DisplayName(
        """
        Testing of the 5th state of a finite state machine (the "state5" method).
        """
    )
    fun testState5() {
        testReadSingleToken("/", Token(Token.Kind.OP, "/"))

        testReadMultipleTokens("/1", listOf(
            Token(Token.Kind.OP, "/"),
            Token(Token.Kind.INT, "1"),
        ))
        testReadMultipleTokens("/ \t", listOf(
            Token(Token.Kind.OP, "/"),
            Token(Token.Kind.SPACES, " \t"),
        ))

        testReadInvalidTokens("/.", "illegal character '.'")
    }

    @Test
    @DisplayName(
        """
        Testing of the 6th state of a finite state machine (the "state6" method).
        Testing reading commands.
        """
    )
    fun testState6() {
        testReadSingleToken("/exit", Token(Token.Kind.COMMAND, "/exit"))
        testReadSingleToken("/help", Token(Token.Kind.COMMAND, "/help"))
        testReadSingleToken("/help1", Token(Token.Kind.COMMAND, "/help1"))

        testReadMultipleTokens("/exit+", listOf(
            Token(Token.Kind.COMMAND, "/exit"),
            Token(Token.Kind.OP, "+"),
        ))

        testReadInvalidTokens("/exit#", "illegal character '#'")
    }

    @Test
    @DisplayName(
        """
        Testing of the 7th state of a finite state machine (the "state7" method).
        Testing reading spaces.
        """
    )
    fun testState7() {
        testReadSingleToken("   ", Token(Token.Kind.SPACES, "   "))

        testReadMultipleTokens(" /", listOf(
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.OP, "/"),
        ))

        testReadInvalidTokens("   $", "illegal character '$'")
    }

    @Test
    @DisplayName(
        """
        Testing of the 8th state of a finite state machine (the "state8" method).
        """
    )
    fun state8() {
        testReadSingleToken(")", Token(Token.Kind.PARENTHESES, ")"))

        testReadMultipleTokens("))", listOf(
            Token(Token.Kind.PARENTHESES, ")"),
            Token(Token.Kind.PARENTHESES, ")")
        ))
        testReadMultipleTokens(")+", listOf(
            Token(Token.Kind.PARENTHESES, ")"),
            Token(Token.Kind.OP, "+")
        ))
        testReadMultipleTokens("),", listOf(
            Token(Token.Kind.PARENTHESES, ")"),
            Token(Token.Kind.COMMA, ",")
        ))

        testReadMultipleTokens(")1", listOf(
            Token(Token.Kind.PARENTHESES, ")"),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.INT, "1")
        ))
        testReadMultipleTokens(")(", listOf(
            Token(Token.Kind.PARENTHESES, ")"),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.PARENTHESES, "(")
        ))
        testReadMultipleTokens(")e", listOf(
            Token(Token.Kind.PARENTHESES, ")"),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.IDENT, "e")
        ))

        testReadInvalidTokens(").", "illegal character '.'")
    }


    @Test
    @DisplayName(
        """
        Testing the reading expression: "+-+1  / -2 * 3e1\n"
        """
    )
    fun testExpression1() {
        testReadMultipleTokens("+-+1  / -2 * 3e1\n", listOf(
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.INT, "1"),
            Token(Token.Kind.SPACES, "  "),
            Token(Token.Kind.OP, "/"),
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.INT, "2"),
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.INT, "3"),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.IDENT, "e1"),
            Token(Token.Kind.EOL, "\n")
        ))
    }

    @Test
    @DisplayName(
        """
        Testing the reading expression: "+-+1  /exit \t -2 / 3E-a1b\n"
        """
    )
    fun testExpression2() {
        testReadMultipleTokens("+-+1  /exit \t -2 / 3E-a1b(1)2\n", listOf(
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.INT, "1"),
            Token(Token.Kind.SPACES, "  "),
            Token(Token.Kind.COMMAND, "/exit"),
            Token(Token.Kind.SPACES, " \t "),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.INT, "2"),
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.OP, "/"),
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.INT, "3"),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.IDENT, "E"),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.IDENT, "a1b"),
            Token(Token.Kind.PARENTHESES, "("),
            Token(Token.Kind.INT, "1"),
            Token(Token.Kind.PARENTHESES, ")"),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.INT, "2"),
            Token(Token.Kind.EOL, "\n")
        ))
    }

    @Test
    @DisplayName(
        """
        Testing the reading expression: "+--+1 ++++ ---3e1 +-+ 3eA"
        """
    )
    fun testExpression3() {
        testReadMultipleTokens("+--+1 ++++ ---3e1 +-+ 3eA", listOf(
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.INT, "1"),
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.INT, "3"),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.IDENT, "e1"),
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.OP, "-"),
            Token(Token.Kind.OP, "+"),
            Token(Token.Kind.SPACES, " "),
            Token(Token.Kind.INT, "3"),
            Token(Token.Kind.OP, "*"),
            Token(Token.Kind.IDENT, "eA"),
        ))
    }

    @Test
    @Disabled
    @DisplayName(
        """
        Testing the reading a long identifier.
        """
    )
    fun testLongIdentifier() {
        val longIdentifier = "a".repeat(10000)
        testReadSingleToken(longIdentifier, Token(Token.Kind.IDENT, longIdentifier))
    }


    private fun testReadSingleToken(inputString: String, expectedToken: Token) {
        val tokenStream = Tokenizer(StringReader(inputString))
        val token = tokenStream.next()

        tokenStream.use {
            assertEquals(
                expectedToken, token,
                "The actual token is not equal to the expected one"
            )
        }
    }

    private fun testReadMultipleTokens(inputString: String, expectedTokens: List<Token>) {
        val tokenStream = Tokenizer(StringReader(inputString))
        val tokens = mutableListOf<Token>()

        tokenStream.forEach { token -> tokens.add(token) }

        tokenStream.use {
            assertEquals(
                expectedTokens.count(), tokens.count(),
                "The actual count of tokens is not equal to the expected one"
            )

            expectedTokens.zip(tokens).forEach {
                assertEquals(
                    it.first, it.second,
                    "The actual token is not equal to the expected one"
                )
            }
        }
    }

    private fun testReadInvalidTokens(inputString: String, expectedMessage: String) {
        val tokenStream = Tokenizer(StringReader(inputString))

        tokenStream.use {
            val exception = assertThrows<SyntaxException> { tokenStream.forEach { _ -> } }

            assertEquals(
                expectedMessage, exception.message,
                "The actual exception message is not equal to the expected one"
            )
        }
    }
}
