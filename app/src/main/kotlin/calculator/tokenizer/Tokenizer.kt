package calculator.tokenizer

import calculator.SyntaxException
import java.io.Closeable
import java.io.Reader

internal class Tokenizer(reader: Reader) : Closeable, Iterator<Token> {
    private val charStream = CharStream(reader)
    private val tokenStack = ArrayDeque<Token>(1)


    override fun next(): Token {
        if (!tokenStack.isEmpty()) {
            return tokenStack.removeFirst()
        }

        return when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.EOF, "")
            LF -> Token(Token.Kind.EOL, "\n")
            in '0'..'9' -> state1(char.toStringBuilder())
            '.' -> state2(".".toStringBuilder())
            '_', in 'a'..'z', in 'A'..'Z' -> state4(char.toStringBuilder())
            '+', '-', '*', '^', '!', '%' -> Token(Token.Kind.OP, char.toString())
            '/' -> state5(char.toStringBuilder())
            ',' -> Token(Token.Kind.COMMA, ",")
            '=' -> Token(Token.Kind.ASSIGN, "=")
            TAB, VT, FF, SPACE -> state7(char.toStringBuilder())
            '(' -> Token(Token.Kind.PARENTHESES, char.toString())
            ')' -> state8(char.toStringBuilder())
            else -> throw SyntaxException("illegal character '$char'")
        }
    }

    override fun hasNext(): Boolean {
        val token = next()

        tokenStack.addFirst(token)
        return token.kind != Token.Kind.EOF
    }

    fun pushBack(token: Token) = tokenStack.addFirst(token)

    override fun close() = charStream.close()


    private tailrec fun state1(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.INT, curLexem.toString())
            in '0'..'9' -> state1(curLexem.append(char))
            '.' -> state3(curLexem.append(char))

            LF, '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, ')' -> {
                charStream.unread(char)
                Token(Token.Kind.INT, curLexem.toString())
            }

            '_', in 'a'..'z', in 'A'..'Z', '(' -> {
                charStream.unread(char)
                charStream.unread('*')
                Token(Token.Kind.INT, curLexem.toString())
            }

            else -> throw SyntaxException("illegal character '$char'")
        }

    private fun state2(curLexem: StringBuilder) =
        when (val char = charStream.read()) {
            in '0'..'9' -> state3(curLexem.append(char))
            else -> throw SyntaxException("illegal character '.'")
        }

    private tailrec fun state3(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.FLOAT, curLexem.toString())
            in '0'..'9' -> state3(curLexem.append(char))

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, ')' -> {
                charStream.unread(char)
                Token(Token.Kind.FLOAT, curLexem.toString())
            }

            '_', in 'a'..'z', in 'A'..'Z', '(' -> {
                charStream.unread(char)
                charStream.unread('*')
                Token(Token.Kind.FLOAT, curLexem.toString())
            }

            else -> throw SyntaxException("illegal character '$char'")
        }

    private tailrec fun state4(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.IDENT, curLexem.toString())

            in '0'..'9', '_', in 'a'..'z',
            in 'A'..'Z' -> state4(curLexem.append(char))

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(Token.Kind.IDENT, curLexem.toString())
            }

            else -> throw SyntaxException("illegal character '$char'")
        }

    private fun state5(curLexem: StringBuilder) =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.OP, "/")
            '_', in 'a'..'z', in 'A'..'Z' -> state6(curLexem.append(char))

            LF, in '0'..'9', '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(Token.Kind.OP, "/")
            }

            else -> throw SyntaxException("illegal character '$char'")
        }

    private tailrec fun state6(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.COMMAND, curLexem.toString())

            in '0'..'9', '_', in 'a'..'z',
            in 'A'..'Z' -> state6(curLexem.append(char))

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, '(', ')' -> {
                charStream.unread(char)
                Token(Token.Kind.COMMAND, curLexem.toString())
            }

            else -> throw SyntaxException("illegal character '$char'")
        }

    private tailrec fun state7(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.SPACES, curLexem.toString())
            TAB, VT, FF, SPACE -> state7(curLexem.append(char))

            LF, in '0'..'9', '.', '_', in 'a'..'z', in 'A'..'Z',
            '+', '-', '*', '^', '!', '%', '/', ',', '=', '(', ')' -> {
                charStream.unread(char)
                Token(Token.Kind.SPACES, curLexem.toString())
            }

            else -> throw SyntaxException("illegal character '$char'")
        }

    private fun state8(curLexem: StringBuilder): Token =
        when (val char = charStream.read()) {
            EOF -> Token(Token.Kind.PARENTHESES, ")")

            LF, '.', '+', '-', '*', '^', '!', '%',
            '/', ',', '=', TAB, VT, FF, SPACE, ')' -> {
                charStream.unread(char)
                Token(Token.Kind.PARENTHESES, curLexem.toString())
            }

            in '0'..'9',
            '_', in 'a'..'z', in 'A'..'Z', '(' -> {
                charStream.unread(char)
                charStream.unread('*')
                Token(Token.Kind.PARENTHESES, curLexem.toString())
            }

            else -> throw SyntaxException("illegal character '$char'")
        }
}
