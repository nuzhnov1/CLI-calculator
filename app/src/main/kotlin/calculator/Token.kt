package calculator

/**
 * A structure containing the token type and the string representation of the token.
 *
 * @param kind token type.
 * @param lexem string representation.
 */
internal data class Token(val kind: Kind, val lexem: String) : GrammarSymbol {
    enum class Kind {
        EOF, EOL, NUMBER, IDENT, OP,
        ASSIGN, SPACES, PARENTHESES,
        COMMAND
    }
}
