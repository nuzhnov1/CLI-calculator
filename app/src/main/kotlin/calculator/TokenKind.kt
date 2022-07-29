package calculator

enum class TokenKind {
    // Lexis tokens:
    EOF, EOL, INT, FLOAT, IDENT, OP,
    COMMA, ASSIGN, SPACES, PARENTHESES,
    COMMAND,

    // Action tokens:
    INVOKE, PUT_ARG
}
