package calculator


internal const val EOF      = '\u0000'  // End of file
internal const val TAB      = '\t'      // Horizontal Tabulation
internal const val LF       = '\n'      // Line feed
internal const val VT       = '\u000b'  // Vertical tabulation
internal const val FF       = '\u000c'  // Form feed
internal const val CR       = '\u000d'  // Carriage return
internal const val SPACE    = ' '       // Single space

internal const val PUSHBACK_BUFFER_SIZE = 2


internal fun Char.toStringBuilder() = StringBuilder(toString())
internal fun String.toStringBuilder() = StringBuilder(this)
