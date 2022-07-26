package calculator

enum class TokenType {
    EOF, EOL, INT, FLOAT, IDENT, OP,
    COMMA, ASSIGN, SPACES, PARENTHESES,
    COMMAND
}
