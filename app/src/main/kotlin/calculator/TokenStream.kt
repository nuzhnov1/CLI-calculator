package calculator

import java.io.Closeable

class TokenStream(charStream: CharStream): Closeable, Iterator<Token> {
    private val charReader = charStream
    private val queue = ArrayDeque<Token>(1)

    val currentLine
        get() = charReader.lineNumber
    val currentPosition
        get() = charReader.position


    override fun next(): Token {
        if (!queue.isEmpty()) {
            return queue.removeLast()
        }

        return when (val char = charReader.readChar()) {
            EOF -> Token(TokenType.EOF, "")
            LF -> Token(TokenType.EOL, "\n")
            in '0'..'9' -> state1(char.toStringBuilder())
            '.' -> state2(".".toStringBuilder())
            '_', in 'a'..'z', in 'A'..'Z' -> state4(char.toStringBuilder())
            '+', '-', '*', '^', '!', '%' -> Token(TokenType.OP, char.toString())
            '/' -> state5(char.toStringBuilder())
            ',' -> Token(TokenType.COLON, ",")
            '=' -> Token(TokenType.ASSIGN, "=")
            TAB, VT, FF, SPACE -> state7(char.toStringBuilder())
            '(', ')' -> Token(TokenType.PARENTHESES, char.toString())
            else -> errorReport("illegal character '$char'")
        }
    }

    fun unread(token: Token) = queue.addLast(token)

    override fun hasNext(): Boolean {
        val token = next()

        queue.addLast(token)
        return token.tokenType != TokenType.EOF
    }

    override fun close() = charReader.close()


    private fun errorReport(message: String): Nothing {
        throw SyntaxException("Syntax error: $message on ${currentPosition - 1} position")
    }

    private tailrec fun state1(curLexem: StringBuilder): Token =
        when (val char = charReader.readChar()) {
            EOF -> Token(TokenType.INT, curLexem.toString())
            in '0'..'9' -> state1(curLexem.append(char))
            '.' -> state3(curLexem.append(char))

            LF, '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, ')' -> {
                charReader.unread(char)
                Token(TokenType.INT, curLexem.toString())
            }

            '_', in 'a'..'z', in 'A'..'Z', '(' -> {
                charReader.unread(char)
                charReader.unread('*')
                Token(TokenType.INT, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }

    private fun state2(curLexem: StringBuilder): Token =
        when (val char = charReader.readChar()) {
            in '0'..'9' -> state3(curLexem.append(char))
            else -> errorReport("illegal character '.'")
        }

    private tailrec fun state3(curLexem: StringBuilder): Token =
        when (val char = charReader.readChar()) {
            EOF -> Token(TokenType.FLOAT, curLexem.toString())
            in '0'..'9' -> state3(curLexem.append(char))

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, ')' -> {
                charReader.unread(char)
                Token(TokenType.FLOAT, curLexem.toString())
            }

            '_', in 'a'..'z', in 'A'..'Z', '(' -> {
                charReader.unread(char)
                charReader.unread('*')
                Token(TokenType.FLOAT, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }

    private tailrec fun state4(curLexem: StringBuilder): Token =
        when (val char = charReader.readChar()) {
            EOF -> Token(TokenType.IDENT, curLexem.toString())

            in '0'..'9', '_', in 'a'..'z',
            in 'A'..'Z' -> state4(curLexem.append(char))

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charReader.unread(char)
                Token(TokenType.IDENT, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }

    private fun state5(curLexem: StringBuilder): Token =
        when (val char = charReader.readChar()) {
            EOF -> Token(TokenType.OP, "/")
            '_', in 'a'..'z', in 'A'..'Z' -> state6(curLexem.append(char))

            LF, in '0'..'9', '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charReader.unread(char)
                Token(TokenType.OP, "/")
            }

            else -> errorReport("illegal character '$char'")
        }

    private tailrec fun state6(curLexem: StringBuilder): Token =
        when (val char = charReader.readChar()) {
            EOF -> Token(TokenType.COMMAND, curLexem.toString())

            in '0'..'9', '_', in 'a'..'z',
            in 'A'..'Z' -> state6(curLexem.append(char))

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charReader.unread(char)
                Token(TokenType.COMMAND, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }

    private tailrec fun state7(curLexem: StringBuilder): Token =
        when (val char = charReader.readChar()) {
            EOF -> Token(TokenType.SPACES, curLexem.toString())
            TAB, VT, FF, SPACE -> state7(curLexem.append(char))

            LF, in '0'..'9', '.', '_', in 'a'..'z', in 'A'..'Z',
            '+', '-', '*', '^', '!', '%', '/', ',', '=', '(', ')' -> {
                charReader.unread(char)
                Token(TokenType.SPACES, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }
}
