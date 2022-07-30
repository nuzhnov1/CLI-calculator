package calculator

import java.io.Reader

internal class Parser {
    private lateinit var tokenizer: Tokenizer
    private lateinit var postfixRecord: MutableList<Token>
    private lateinit var curToken: Token


    fun parse(reader: Reader): List<Token> {
        tokenizer = Tokenizer(reader)
        postfixRecord = mutableListOf()
        curToken = readToken()

        tokenizer.use { parseStatement() }

        return postfixRecord.toList()
    }


    private fun readToken(): Token {
        var token = tokenizer.next()

        // Skipping the whitespace tokens:
        while (token.tokenKind == TokenKind.SPACES) {
            token = tokenizer.next()
        }

        return token
    }

    private fun readTokenInExpr(): Token {
        val token = readToken()

        return if (token.tokenKind == TokenKind.COMMAND) {
            // Splitting the command token into a division operation and an identifier:
            val lexem = token.lexem

            // Pushing back identifier:
            tokenizer.pushBack(Token(TokenKind.IDENT, lexem.substring(1)))
            // Returning the token of the division operation:
            Token(TokenKind.OP, lexem.substring(0, 1))
        } else {
            token
        }
    }

    private fun errorReport(expectedInput: String, actualToken: Token): Nothing {
        when (actualToken.tokenKind) {
            TokenKind.EOF, TokenKind.EOL -> throw SyntaxException(
                "expected $expectedInput, got end of line"
            )

            else -> throw SyntaxException(
                "expected $expectedInput, got '${actualToken.lexem}'"
            )
        }
    }

    private fun parseStatement() {
        when (curToken.tokenKind) {
            TokenKind.EOF, TokenKind.EOL -> return

            TokenKind.INT, TokenKind.FLOAT -> {
                parseExpr()
                parseEndLine()
            }

            TokenKind.IDENT -> {
                val nextToken = readToken()

                if (nextToken.tokenKind == TokenKind.ASSIGN) {
                    postfixRecord.add(curToken)
                    curToken = readTokenInExpr()
                    parseExpr()
                    postfixRecord.add(Token(TokenKind.ASSIGN, "="))
                    parseEndLine()
                } else {
                    tokenizer.pushBack(nextToken)
                    parseExpr()
                    parseEndLine()
                }
            }

            TokenKind.OP -> {
                if (curToken.lexem == "+" || curToken.lexem == "-") {
                    parseExpr()
                    parseEndLine()
                } else {
                    errorReport("expression or command", curToken)
                }
            }

            TokenKind.PARENTHESES -> {
                if (curToken.lexem == "(") {
                    parseExpr()
                    parseEndLine()
                } else {
                    errorReport("expression or command", curToken)
                }
            }

            TokenKind.COMMAND -> {
                val commandName = curToken.lexem.substring(1)

                postfixRecord.add(Token(TokenKind.COMMAND, commandName))
                curToken = readToken()
                parseEndLine()
            }

            else -> errorReport("expression or command", curToken)
        }
    }

    private fun parseEndLine() {
        if (!(curToken.tokenKind == TokenKind.EOF || curToken.tokenKind == TokenKind.EOL)) {
            errorReport("end of line", curToken)
        }
    }

    private fun parseExpr() {
        parseExprPriority5()
        parseExprRest()
    }

    private tailrec fun parseExprRest(): Unit =
        when (curToken.lexem) {
            "+", "-" -> {
                val lexem = curToken.lexem

                curToken = readTokenInExpr()
                parseExprPriority5()
                postfixRecord.add(Token(TokenKind.OP, lexem))
                parseExprRest()
            }

            else -> Unit
        }

    private fun parseExprPriority5() {
        parseExprPriority4()
        parseExprPriority5Rest()
    }

    private tailrec fun parseExprPriority5Rest(): Unit =
        when (curToken.lexem) {
            "*", "/" -> {
                val lexem = curToken.lexem

                curToken = readTokenInExpr()
                parseExprPriority4()
                postfixRecord.add(Token(TokenKind.OP, lexem))
                parseExprPriority5Rest()
            }

            else -> Unit
        }

    private fun parseExprPriority4() {
        if (curToken.lexem == "+" || curToken.lexem == "-") {
            val lexem = curToken.lexem

            curToken = readTokenInExpr()
            parseExprPriority4()
            // Adding the prefix "u" to the operator to distinguish between unary and binary operations:
            postfixRecord.add(Token(TokenKind.OP, "u$lexem"))
        } else {
            parseExprPriority3()
        }
    }

    private fun parseExprPriority3() {
        parseExprPriority2()
        parseExprPriority3Rest()
    }

    private tailrec fun parseExprPriority3Rest(): Unit =
        when (curToken.lexem) {
            "^" -> {
                curToken = readTokenInExpr()
                parseExprPriority4()
                postfixRecord.add(Token(TokenKind.OP, "^"))
                parseExprPriority3Rest()
            }

            else -> Unit
        }

    private fun parseExprPriority2() {
        parseExprPriority1()
        parseExprPriority2Rest()
    }

    private tailrec fun parseExprPriority2Rest(): Unit =
        when (curToken.lexem) {
            "!", "%" -> {
                val lexem = curToken.lexem

                curToken = readTokenInExpr()
                postfixRecord.add(Token(TokenKind.OP, lexem))
                parseExprPriority2Rest()
            }

            else -> Unit
        }

    private fun parseExprPriority1() {
        when (curToken.tokenKind) {
            TokenKind.INT, TokenKind.FLOAT -> {
                postfixRecord.add(curToken)
                curToken = readTokenInExpr()
            }

            TokenKind.IDENT -> {
                postfixRecord.add(curToken)
                curToken = readTokenInExpr()
                parseFunctionCallOrEpsilon()
            }

            TokenKind.PARENTHESES -> {
                if (curToken.lexem == "(") {
                    curToken = readTokenInExpr()
                    parseExpr()

                    if (curToken.lexem != ")") {
                        errorReport("')'", curToken)
                    } else {
                        curToken = readTokenInExpr()
                    }
                } else {
                    errorReport("expression", curToken)
                }
            }

            else -> errorReport("expression", curToken)
        }
    }

    private fun parseFunctionCallOrEpsilon() {
        if (curToken.lexem == "(") {
            curToken = readTokenInExpr()

            curToken = if (curToken.lexem == ")") {
                readTokenInExpr()
            } else {
                parseActualArgumentsList()

                if (curToken.lexem != ")") {
                    errorReport("')'", curToken)
                } else {
                    readTokenInExpr()
                }
            }

            postfixRecord.add(Token(TokenKind.ACTION, "invoke"))
        }
    }

    private fun parseActualArgumentsList() {
        parseExpr()
        postfixRecord.add(Token(TokenKind.ACTION, "put_arg"))
        parseActualArgumentsListRest()
    }

    private tailrec fun parseActualArgumentsListRest(): Unit =
        when (curToken.tokenKind) {
            TokenKind.COMMA -> {
                curToken = readTokenInExpr()
                parseExpr()
                postfixRecord.add(Token(TokenKind.ACTION, "put_arg"))
                parseActualArgumentsListRest()
            }

            else -> Unit
        }
}
