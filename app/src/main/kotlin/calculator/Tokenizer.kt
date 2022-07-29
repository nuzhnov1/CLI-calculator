package calculator

import java.io.Closeable

class Tokenizer(private val charStream: CharStream) : Closeable, Iterator<Token> {
    private val tokenStack = ArrayDeque<Token>(1)


    override fun next(): Token {
        if (!tokenStack.isEmpty()) {
            return tokenStack.removeLast()
        }

        return when (val char = charStream.read()) {
            EOF -> Token(TokenKind.EOF, "")
            LF -> Token(TokenKind.EOL, "\n")
            in '0'..'9' -> state1(char.toStringBuilder())
            '.' -> state2(".".toStringBuilder())
            '_', in 'a'..'z', in 'A'..'Z' -> state4(char.toStringBuilder())
            '+', '-', '*', '^', '!', '%' -> Token(TokenKind.OP, char.toString())
            '/' -> state5(char.toStringBuilder())
            ',' -> Token(TokenKind.COMMA, ",")
            '=' -> Token(TokenKind.ASSIGN, "=")
            TAB, VT, FF, SPACE -> state7(char.toStringBuilder())
            '(' -> Token(TokenKind.PARENTHESES, char.toString())
            ')' -> state8(char.toStringBuilder())
            else -> errorReport("illegal character '$char'")
        }
    }

    fun pushBack(token: Token) = tokenStack.addLast(token)

    override fun hasNext(): Boolean {
        val token = next()

        tokenStack.addLast(token)
        return token.tokenKind != TokenKind.EOF
    }

    override fun close() = charStream.close()


    private fun errorReport(message: String): Nothing {
        throw SyntaxException("Syntax error: $message")
    }

    private tailrec fun state1(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(TokenKind.INT, curLexem.toString())
            in '0'..'9' -> state1(curLexem.append(char))
            '.' -> state3(curLexem.append(char))

            LF, '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, ')' -> {
                charStream.unread(char)
                Token(TokenKind.INT, curLexem.toString())
            }

            '_', in 'a'..'z', in 'A'..'Z', '(' -> {
                charStream.unread(char)
                charStream.unread('*')
                Token(TokenKind.INT, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }

    private fun state2(curLexem: StringBuilder) =
        when (val char = charStream.read()) {
            in '0'..'9' -> state3(curLexem.append(char))
            else -> errorReport("illegal character '.'")
        }

    private tailrec fun state3(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(TokenKind.FLOAT, curLexem.toString())
            in '0'..'9' -> state3(curLexem.append(char))

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, ')' -> {
                charStream.unread(char)
                Token(TokenKind.FLOAT, curLexem.toString())
            }

            '_', in 'a'..'z', in 'A'..'Z', '(' -> {
                charStream.unread(char)
                charStream.unread('*')
                Token(TokenKind.FLOAT, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }

    private tailrec fun state4(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(TokenKind.IDENT, curLexem.toString())

            in '0'..'9', '_', in 'a'..'z',
            in 'A'..'Z' -> state4(curLexem.append(char))

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(TokenKind.IDENT, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }

    private fun state5(curLexem: StringBuilder) =
        when (val char = charStream.read()) {
            EOF -> Token(TokenKind.OP, "/")
            '_', in 'a'..'z', in 'A'..'Z' -> state6(curLexem.append(char))

            LF, in '0'..'9', '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(TokenKind.OP, "/")
            }

            else -> errorReport("illegal character '$char'")
        }

    private tailrec fun state6(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(TokenKind.COMMAND, curLexem.toString())

            in '0'..'9', '_', in 'a'..'z',
            in 'A'..'Z' -> state6(curLexem.append(char))

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(TokenKind.COMMAND, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }

    private tailrec fun state7(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(TokenKind.SPACES, curLexem.toString())
            TAB, VT, FF, SPACE -> state7(curLexem.append(char))

            LF, in '0'..'9', '.', '_', in 'a'..'z', in 'A'..'Z',
            '+', '-', '*', '^', '!', '%', '/', ',', '=', '(', ')' -> {
                charStream.unread(char)
                Token(TokenKind.SPACES, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }

    private fun state8(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(TokenKind.PARENTHESES, ")")

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, ')' -> {
                charStream.unread(char)
                Token(TokenKind.PARENTHESES, curLexem.toString())
            }

            in '0'..'9',
            '_', in 'a'..'z', in 'A'..'Z', '(' -> {
                charStream.unread(char)
                charStream.unread('*')
                Token(TokenKind.PARENTHESES, curLexem.toString())
            }

            else -> errorReport("illegal character '$char'")
        }
}
