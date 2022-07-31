package calculator

import java.io.Reader

internal class Parser {
    private enum class NonTerminal : GrammarSymbol {
        // Regular non-terminals:
        Expr, ExprRest, ExprPriority5, ExprPriority5Rest,
        ExprPriority4, ExprPriority3, ExprPriority3Rest,
        ExprPriority2, ExprPriority2Rest, ExprPriority1,
        FunctionCallOrEpsilon, ActualArgumentsList,
        ActualArgumentsListRest,

        // Non-terminals of actions:
        AddBinPlusToPostfixRecord, AddBinMinusToPostfixRecord,
        AddUnPlusToPostfixRecord, AddUnMinusToPostfixRecord,
        AddMulToPostfixRecord, AddDivToPostfixRecord,
        AddPowToPostfixRecord, AddInvokeActionToPostfixRecord,
        AddPutArgActionToPostfixRecord
    }


    private lateinit var tokenizer: Tokenizer
    private lateinit var postfixRecord: MutableList<Token>
    private lateinit var curToken: Token
    private lateinit var symbolsStack: ArrayDeque<GrammarSymbol>


    fun parse(reader: Reader): List<Token> {
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
        while (token.tokenKind == TokenKind.SPACES) {
            token = tokenizer.next()
        }

        return token
    }

    private fun readTokenInExpr(): Token {
        val token = readToken()

        return if (token.tokenKind == TokenKind.COMMAND) {
            // Splitting the command token into a division operation and an identifier
            // For example: "/abc" -> "/" and "abc":
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
        symbolsStack.addFirst(NonTerminal.Expr)

        while (!symbolsStack.isEmpty()) {
            when (val symbol = symbolsStack.removeFirst()) {
                // Processing regular non-terminals:

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
                NonTerminal.FunctionCallOrEpsilon -> parseFunctionCallOrEpsilon()
                NonTerminal.ActualArgumentsList -> parseActualArgumentsList()
                NonTerminal.ActualArgumentsListRest -> parseActualArgumentsListRest()

                // Processing non-terminals of actions:

                NonTerminal.AddBinPlusToPostfixRecord -> postfixRecord.add(Token(TokenKind.OP, "+"))
                NonTerminal.AddBinMinusToPostfixRecord -> postfixRecord.add(Token(TokenKind.OP, "-"))
                NonTerminal.AddUnPlusToPostfixRecord -> postfixRecord.add(Token(TokenKind.OP, "u+"))
                NonTerminal.AddUnMinusToPostfixRecord -> postfixRecord.add(Token(TokenKind.OP, "u-"))
                NonTerminal.AddMulToPostfixRecord -> postfixRecord.add(Token(TokenKind.OP, "*"))
                NonTerminal.AddDivToPostfixRecord -> postfixRecord.add(Token(TokenKind.OP, "/"))
                NonTerminal.AddPowToPostfixRecord -> postfixRecord.add(Token(TokenKind.OP, "^"))
                NonTerminal.AddInvokeActionToPostfixRecord -> postfixRecord.add(Token(TokenKind.ACTION, "invoke"))
                NonTerminal.AddPutArgActionToPostfixRecord -> postfixRecord.add(Token(TokenKind.ACTION, "put_arg"))

                // Processing terminals (tokens):

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
        }
    }

    private fun parseExprPriority4() {
        when (curToken.lexem) {
            "+" -> {
                curToken = readTokenInExpr()
                symbolsStack.addFirst(NonTerminal.AddUnPlusToPostfixRecord)
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
        while (curToken.lexem == "!" || curToken.lexem == "%") {
            postfixRecord.add(curToken)
            curToken = readTokenInExpr()
        }
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
                    symbolsStack.addFirst(Token(TokenKind.PARENTHESES, ")"))
                    symbolsStack.addFirst(NonTerminal.Expr)
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

            if (curToken.lexem == ")") {
                curToken = readTokenInExpr()
                postfixRecord.add(Token(TokenKind.ACTION, "invoke"))
            } else {
                symbolsStack.addFirst(NonTerminal.AddInvokeActionToPostfixRecord)
                symbolsStack.addFirst(Token(TokenKind.PARENTHESES, ")"))
                symbolsStack.addFirst(NonTerminal.ActualArgumentsList)
            }
        }
    }

    private fun parseActualArgumentsList() {
        symbolsStack.addFirst(NonTerminal.ActualArgumentsListRest)
        symbolsStack.addFirst(NonTerminal.AddPutArgActionToPostfixRecord)
        symbolsStack.addFirst(NonTerminal.Expr)
    }

    private fun parseActualArgumentsListRest() {
        if (curToken.tokenKind == TokenKind.COMMA) {
            curToken = readTokenInExpr()
            symbolsStack.addFirst(NonTerminal.ActualArgumentsListRest)
            symbolsStack.addFirst(NonTerminal.AddPutArgActionToPostfixRecord)
            symbolsStack.addFirst(NonTerminal.Expr)
        }
    }
}
