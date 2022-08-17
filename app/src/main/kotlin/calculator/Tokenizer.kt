package calculator

import java.io.Closeable
import java.io.Reader

/**
 * Reads characters from the [Reader], converts them into tokens
 * and allows to return the read token to the input by placing them on the LIFO stack.
 * Each token is an instance of the [Token] class.
 *
 * @param reader where will the characters be read from.
 */
internal class Tokenizer(reader: Reader) : Iterator<Token>, Closeable {
    private val charStream = CharStream(reader)
    private val tokenStack = ArrayDeque<Token>(1)


    /**
     * Reads a single token.
     *
     * @throws SyntaxException If an illegal character occurs.
     * @throws java.io.IOException If an I/O error occurs.
     */
    override fun next(): Token {
        if (!tokenStack.isEmpty()) {
            return tokenStack.removeFirst()
        }

        return when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.EOF, "")
            LF -> Token(Token.Kind.EOL, "\n")
            in '0'..'9' -> state1(char.toStringBuilder())
            in 'a'..'z', in 'A'..'Z' -> state2(char.toStringBuilder())
            '+', '-', '*', '%', '^', '!' -> Token(Token.Kind.OP, char.toString())
            '/' -> state3(char.toStringBuilder())
            '=' -> Token(Token.Kind.ASSIGN, "=")
            TAB, VT, FF, SPACE -> state5(char.toStringBuilder())
            '(', ')' -> Token(Token.Kind.PARENTHESES, char.toString())
            else -> throw SyntaxException("Illegal character '$char'")
        }
    }

    /**
     * Returns true if the next token is not an EOF token.
     *
     * @throws SyntaxException If an illegal character occurs.
     * @throws java.io.IOException If an I/O error occurs.
     */
    override fun hasNext(): Boolean {
        val token = next()

        tokenStack.addFirst(token)
        return token.kind != Token.Kind.EOF
    }

    override fun close() {
        charStream.close()
    }

    /**
     * Return a single token to the input by placing them on the LIFO stack.
     */
    fun pushback(token: Token) {
        tokenStack.addFirst(token)
    }


    private tailrec fun state1(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.NUMBER, curLexem.toString())
            in '0'..'9' -> state1(curLexem.append(char))
            in 'a'..'z', in 'A'..'Z' -> throw SyntaxException("Invalid number")

            LF, '+', '-', '*', '%', '^', '!', '/', '=',
            TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(Token.Kind.NUMBER, curLexem.toString())
            }

            else -> throw SyntaxException("Illegal character '$char'")
        }

    private tailrec fun state2(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.IDENT, curLexem.toString())
            in '0'..'9' -> throw SyntaxException("Invalid identifier")
            in 'a'..'z', in 'A'..'Z' -> state2(curLexem.append(char))

            LF, '+', '-', '*', '%', '^', '!',
            '/', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(Token.Kind.IDENT, curLexem.toString())
            }

            else -> throw SyntaxException("Illegal character '$char'")
        }

    private fun state3(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.OP, "/")
            in 'a'..'z', in 'A'..'Z' -> state4(curLexem.append(char))

            LF, in '0'..'9', '+', '-', '*', '%', '^', '!',
            '/', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(Token.Kind.OP, "/")
            }

            else -> throw SyntaxException("Illegal character '$char'")
        }

    private tailrec fun state4(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.COMMAND, curLexem.toString())
            in 'a'..'z', in 'A'..'Z' -> state4(curLexem.append(char))

            LF, in '0'..'9', '+', '-', '*', '%', '^', '!',
            '/', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(Token.Kind.COMMAND, curLexem.toString())
            }

            else -> throw SyntaxException("Illegal character '$char'")
        }

    private tailrec fun state5(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.SPACES, curLexem.toString())
            TAB, VT, FF, SPACE -> state5(curLexem.append(char))

            LF, in '0'..'9', in 'a'..'z', in 'A'..'Z',
            '+', '-', '*', '%', '^', '!', '/', '=', '(', ')' -> {
                charStream.unread(char)
                Token(Token.Kind.SPACES, curLexem.toString())
            }

            else -> throw SyntaxException("Illegal character '$char'")
        }

    private fun Char.toStringBuilder() = StringBuilder(toString())
}
