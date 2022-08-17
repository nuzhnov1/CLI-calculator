package calculator

/**
 * A structure containing the postfix token type and the string representation of this token.
 *
 * @param kind token type.
 * @param lexem string representation.
 */
internal data class PostfixItem(val kind: Kind, val lexem: String) {
    enum class Kind {
        NUMBER, IDENT, OP, ASSIGN, COMMAND
    }
}
