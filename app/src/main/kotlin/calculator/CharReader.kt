package calculator

import java.io.Closeable
import java.io.LineNumberReader
import java.io.PushbackReader
import java.io.Reader

class CharStream(reader: Reader, private val tabSize: Int = 4): Closeable {
    private val lineNumberReader = LineNumberReader(reader)
    private val pushbackReader = PushbackReader(lineNumberReader, PUSHBACK_BUFFER_SIZE)

    var lineNumber
        get() = lineNumberReader.lineNumber
        set(value) { lineNumberReader.lineNumber = value }
    var position = 1


    init {
        if (tabSize < 1) {
            throw IllegalArgumentException("tabSize < 1")
        }

        lineNumber = 1
    }


    fun readChar(): Char {
        val code = pushbackReader.read()
        val char = if (code < 0) { EOF } else { code.toChar() }

        when {
            char == LF -> position = 1
            char == TAB -> position += (tabSize - (position - 1) % tabSize)
            char != EOF -> position++
        }

        return char
    }

    fun unread(char: Char) = pushbackReader.unread(char.code)

    override fun close() = pushbackReader.close()
}
