package calculator

import java.io.Closeable
import java.io.LineNumberReader
import java.io.PushbackReader
import java.io.Reader

internal class CharStream(reader: Reader) : Closeable {
    private val pushbackReader = PushbackReader(LineNumberReader(reader), PUSHBACK_BUFFER_SIZE)


    fun read(): Char {
        val code = pushbackReader.read()
        return if (code < 0) { EOF } else { code.toChar() }
    }

    fun unread(char: Char) = pushbackReader.unread(char.code)

    override fun close() = pushbackReader.close()
}
