package calculator

import java.io.Reader

/**
 * Reads tokens, parsing statement by statement and returns a list of postfix tokens.
 */
internal class Parser {
    /**
     * Enumeration of non-terminals of the calculator grammar.
     */
    private enum class NonTerminal : GrammarSymbol {
        // Regular non-terminals:
        Expr, ExprRest, ExprPriority5, ExprPriority5Rest,
        ExprPriority4, ExprPriority3, ExprPriority3Rest,
        ExprPriority2, ExprPriority2Rest, ExprPriority1,

        // Non-terminals of actions:
        AddBinPlusToPostfixRecord, AddBinMinusToPostfixRecord,
        AddUnMinusToPostfixRecord,
        AddMulToPostfixRecord, AddDivToPostfixRecord, AddModToPostfixRecord,
        AddPowToPostfixRecord
    }


    private lateinit var tokenizer: Tokenizer
    private lateinit var postfixRecord: MutableList<PostfixItem>
    private lateinit var curToken: Token
    private lateinit var symbolsStack: ArrayDeque<GrammarSymbol>


    /**
     * Parse a single statement, reads characters from the [reader],
     * and returns list of postfix tokens.
     *
     * @throws SyntaxException If a statement was read that
     * does not match the grammar of the calculator or contains illegal characters.
     * @throws java.io.IOException If an I/O error occurs.
     */
    fun parse(reader: Reader): List<PostfixItem> {
        tokenizer = Tokenizer(reader)
        postfixRecord = mutableListOf()
        curToken = readToken()
        symbolsStack = ArrayDeque()

        tokenizer.use { parseStatement() }

        return postfixRecord.toList()
    }


    private fun readToken(): Token {
        var token = tokenizer.next()

        // Skipping the whitespace tokens:
        while (token.kind == Token.Kind.SPACES) {
            token = tokenizer.next()
        }

        return token
    }

    private fun readTokenInExpr(): Token {
        val token = readToken()

        return if (token.kind == Token.Kind.COMMAND) {
            // Splitting the command token into a division operation and an identifier
            // For example: "/abc" -> "/" and "abc":
            val lexem = token.lexem

            // Pushing back identifier:
            tokenizer.pushback(Token(Token.Kind.IDENT, lexem.substring(1)))
            // Returning the token of the division operation:
            Token(Token.Kind.OP, lexem.substring(0, 1))
        } else {
            token
        }
    }

    private fun errorReport(expectedInput: String, actualToken: Token): Nothing {
        when (actualToken.kind) {
            Token.Kind.EOF -> throw SyntaxException("Expected $expectedInput, got end of file")
            Token.Kind.EOL -> throw SyntaxException("Expected $expectedInput, got end of line")
            else -> throw SyntaxException("Expected $expectedInput, got '${actualToken.lexem}'")
        }
    }

    private fun parseStatement() {
        when (curToken.kind) {
            Token.Kind.EOF -> postfixRecord.add(PostfixItem(PostfixItem.Kind.COMMAND, "exit"))
            Token.Kind.EOL -> return

            Token.Kind.NUMBER -> {
                try {
                    parseExpr()
                    parseEndLine()
                } catch (e: SyntaxException) {
                    throw SyntaxException("Invalid expression", e)
                }
            }

            Token.Kind.IDENT -> {
                val nextToken = readToken()

                if (nextToken.kind == Token.Kind.ASSIGN) {
                    try {
                        postfixRecord.add(PostfixItem(PostfixItem.Kind.IDENT, curToken.lexem))
                        curToken = readTokenInExpr()
                        parseExpr()
                        postfixRecord.add(PostfixItem(PostfixItem.Kind.ASSIGN, "="))
                        parseEndLine()
                    } catch (e: SyntaxException) {
                        throw SyntaxException("Invalid assigment", e)
                    }
                } else {
                    try {
                        tokenizer.pushback(nextToken)
                        parseExpr()
                        parseEndLine()
                    } catch (e: SyntaxException) {
                        throw SyntaxException("Invalid expression", e)
                    }
                }
            }

            Token.Kind.OP -> {
                if (curToken.lexem == "+" || curToken.lexem == "-") {
                    try {
                        parseExpr()
                        parseEndLine()
                    } catch (e: SyntaxException) {
                        throw SyntaxException("Invalid expression", e)
                    }
                } else {
                    throw SyntaxException("Invalid expression")
                }
            }

            Token.Kind.PARENTHESES -> {
                if (curToken.lexem == "(") {
                    try {
                        parseExpr()
                        parseEndLine()
                    } catch (e: SyntaxException) {
                        throw SyntaxException("Invalid expression", e)
                    }
                } else {
                    throw SyntaxException("Invalid expression")
                }
            }

            Token.Kind.COMMAND -> {
                try {
                    val commandName = curToken.lexem.substring(1)

                    postfixRecord.add(PostfixItem(PostfixItem.Kind.COMMAND, commandName))
                    curToken = readToken()
                    parseEndLine()
                } catch (e: SyntaxException) {
                    throw SyntaxException("Invalid command", e)
                }
            }

            else -> throw SyntaxException("Invalid expression")
        }
    }

    private fun parseEndLine() {
        if (curToken.kind != Token.Kind.EOL) {
            errorReport("end of line", curToken)
        }
    }

    private fun parseExpr() {
        symbolsStack.addFirst(NonTerminal.Expr)

        while (!symbolsStack.isEmpty()) {
            when (val symbol = symbolsStack.removeFirst()) {
                // Processing of regular non-terminals:

                NonTerminal.Expr -> {
                    symbolsStack.addFirst(NonTerminal.ExprRest)
                    symbolsStack.addFirst(NonTerminal.ExprPriority5)
                }

                NonTerminal.ExprRest -> parseExprRest()
                NonTerminal.ExprPriority5 -> parseExprPriority5()
                NonTerminal.ExprPriority5Rest -> parseExprPriority5Rest()
                NonTerminal.ExprPriority4 -> parseExprPriority4()
                NonTerminal.ExprPriority3 -> parseExprPriority3()
                NonTerminal.ExprPriority3Rest -> parseExprPriority3Rest()
                NonTerminal.ExprPriority2 -> parseExprPriority2()
                NonTerminal.ExprPriority2Rest -> parseExprPriority2Rest()
                NonTerminal.ExprPriority1 -> parseExprPriority1()

                // Processing of non-terminals of actions:

                NonTerminal.AddBinPlusToPostfixRecord -> postfixRecord.add(
                    PostfixItem(PostfixItem.Kind.OP, "+")
                )
                NonTerminal.AddBinMinusToPostfixRecord -> postfixRecord.add(
                    PostfixItem(PostfixItem.Kind.OP, "-")
                )
                NonTerminal.AddUnMinusToPostfixRecord -> postfixRecord.add(
                    PostfixItem(PostfixItem.Kind.OP, "u-")
                )
                NonTerminal.AddMulToPostfixRecord -> postfixRecord.add(
                    PostfixItem(PostfixItem.Kind.OP, "*")
                )
                NonTerminal.AddDivToPostfixRecord -> postfixRecord.add(
                    PostfixItem(PostfixItem.Kind.OP, "/")
                )
                NonTerminal.AddModToPostfixRecord -> postfixRecord.add(
                    PostfixItem(PostfixItem.Kind.OP, "%")
                )
                NonTerminal.AddPowToPostfixRecord -> postfixRecord.add(
                    PostfixItem(PostfixItem.Kind.OP, "^")
                )

                // Processing of terminals (tokens):

                else -> {
                    val expectedToken = symbol as Token

                    if (curToken != expectedToken) {
                        errorReport("'${expectedToken.lexem}'", curToken)
                    } else {
                        curToken = readTokenInExpr()
                    }
                }
            }
        }
    }

    private fun parseExprRest() {
        when (curToken.lexem) {
            "+" -> {
                curToken = readTokenInExpr()
                symbolsStack.addFirst(NonTerminal.ExprRest)
                symbolsStack.addFirst(NonTerminal.AddBinPlusToPostfixRecord)
                symbolsStack.addFirst(NonTerminal.ExprPriority5)
            }

            "-" -> {
                curToken = readTokenInExpr()
                symbolsStack.addFirst(NonTerminal.ExprRest)
                symbolsStack.addFirst(NonTerminal.AddBinMinusToPostfixRecord)
                symbolsStack.addFirst(NonTerminal.ExprPriority5)
            }
        }
    }

    private fun parseExprPriority5() {
        symbolsStack.addFirst(NonTerminal.ExprPriority5Rest)
        symbolsStack.addFirst(NonTerminal.ExprPriority4)
    }

    private fun parseExprPriority5Rest() {
        if (curToken.kind == Token.Kind.OP) {
            when (curToken.lexem) {
                "*" -> {
                    curToken = readTokenInExpr()
                    symbolsStack.addFirst(NonTerminal.ExprPriority5Rest)
                    symbolsStack.addFirst(NonTerminal.AddMulToPostfixRecord)
                    symbolsStack.addFirst(NonTerminal.ExprPriority4)
                }

                "/" -> {
                    curToken = readTokenInExpr()
                    symbolsStack.addFirst(NonTerminal.ExprPriority5Rest)
                    symbolsStack.addFirst(NonTerminal.AddDivToPostfixRecord)
                    symbolsStack.addFirst(NonTerminal.ExprPriority4)
                }

                "%" -> {
                    curToken = readTokenInExpr()
                    symbolsStack.addFirst(NonTerminal.ExprPriority5Rest)
                    symbolsStack.addFirst(NonTerminal.AddModToPostfixRecord)
                    symbolsStack.addFirst(NonTerminal.ExprPriority4)
                }
            }
        }
    }

    private fun parseExprPriority4() {
        when (curToken.lexem) {
            "+" -> {
                curToken = readTokenInExpr()
                symbolsStack.addFirst(NonTerminal.ExprPriority4)
            }

            "-" -> {
                curToken = readTokenInExpr()
                symbolsStack.addFirst(NonTerminal.AddUnMinusToPostfixRecord)
                symbolsStack.addFirst(NonTerminal.ExprPriority4)
            }

            else -> symbolsStack.addFirst(NonTerminal.ExprPriority3)
        }
    }

    private fun parseExprPriority3() {
        symbolsStack.addFirst(NonTerminal.ExprPriority3Rest)
        symbolsStack.addFirst(NonTerminal.ExprPriority2)
    }

    private fun parseExprPriority3Rest() {
        if (curToken.lexem == "^") {
            curToken = readTokenInExpr()
            symbolsStack.addFirst(NonTerminal.ExprPriority3Rest)
            symbolsStack.addFirst(NonTerminal.AddPowToPostfixRecord)
            symbolsStack.addFirst(NonTerminal.ExprPriority4)
        }
    }

    private fun parseExprPriority2() {
        symbolsStack.addFirst(NonTerminal.ExprPriority2Rest)
        symbolsStack.addFirst(NonTerminal.ExprPriority1)
    }

    private fun parseExprPriority2Rest() {
        while (curToken.lexem == "!") {
            postfixRecord.add(PostfixItem(PostfixItem.Kind.OP, curToken.lexem))
            curToken = readTokenInExpr()
        }
    }

    private fun parseExprPriority1() {
        when (curToken.kind) {
            Token.Kind.NUMBER -> {
                postfixRecord.add(PostfixItem(PostfixItem.Kind.NUMBER, curToken.lexem))
                curToken = readTokenInExpr()
            }

            Token.Kind.IDENT -> {
                postfixRecord.add(PostfixItem(PostfixItem.Kind.IDENT, curToken.lexem))
                curToken = readTokenInExpr()
            }

            Token.Kind.PARENTHESES -> {
                if (curToken.lexem == "(") {
                    curToken = readTokenInExpr()
                    symbolsStack.addFirst(Token(Token.Kind.PARENTHESES, ")"))
                    symbolsStack.addFirst(NonTerminal.Expr)
                } else {
                    errorReport("expression", curToken)
                }
            }

            else -> errorReport("expression", curToken)
        }
    }
}
