package calculator.parser

internal data class PostfixItem(val kind: Kind, val lexem: String) {
    internal enum class Kind {
        INT, FLOAT, IDENT, OP, ASSIGN, ACTION, COMMAND
    }
}
