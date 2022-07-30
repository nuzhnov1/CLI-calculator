package calculator

internal enum class TokenKind {
    EOF, EOL, INT, FLOAT, IDENT, OP,
    COMMA, ASSIGN, SPACES, PARENTHESES,
    COMMAND, ACTION
}
