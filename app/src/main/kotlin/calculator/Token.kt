package calculator

internal data class Token(val tokenKind: TokenKind, val lexem: String) : GrammarSymbol
