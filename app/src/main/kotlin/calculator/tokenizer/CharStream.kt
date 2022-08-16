package calculator.tokenizer

import java.io.Closeable
import java.io.LineNumberReader
import java.io.PushbackReader
import java.io.Reader

/**
 * Reads characters and also allows to return
 * them to the input by placing them on the LIFO stack.
 *
 * @param reader where will the characters be read from
 */
internal class CharStream(reader: Reader) : Closeable {
    private val pushbackReader = PushbackReader(LineNumberReader(reader), 1)


    /**
     * Reads a single character or the end of file (EOF).
     * If EOF has been read, it is converted to the NUL character ('\u0000').
     */
    fun read(): Char {
        val code = pushbackReader.read()
        return if (code < 0) { EOF } else { code.toChar() }
    }

    /**
     * Return a single character to the input by placing them on the LIFO stack.
     *
     * @throws java.io.IOException If the pushback buffer is full, or if some other I/O error occurs.
     */
    fun unread(char: Char) {
        pushbackReader.unread(char.code)
    }

    override fun close() {
        pushbackReader.close()
    }
}
