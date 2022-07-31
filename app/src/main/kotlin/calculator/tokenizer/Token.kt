package calculator.tokenizer

import calculator.GrammarSymbol

internal data class Token(val kind: Kind, val lexem: String) : GrammarSymbol {
    internal enum class Kind {
        EOF, EOL, INT, FLOAT, IDENT, OP,
        COMMA, ASSIGN, SPACES, PARENTHESES,
        COMMAND
    }
}
