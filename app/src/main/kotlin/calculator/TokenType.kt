package calculator

enum class TokenType {
    EOF, EOL, INT, FLOAT, IDENT, OP,
    COLON, ASSIGN, SPACES, PARENTHESES,
    COMMAND
}
