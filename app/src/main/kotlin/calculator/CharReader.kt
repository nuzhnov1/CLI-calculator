package calculator

import java.io.LineNumberReader
import java.io.PushbackReader
import java.io.Reader

const val EOF = '\u0000'

class CharStream(reader: Reader, bufferSize: Int): LineNumberReader(reader) {
    private val pushbackReader = PushbackReader(this, bufferSize)
    var charNumber = 0


    init { lineNumber = 1 }


    override fun read(): Int {
        synchronized(lock) {
            val code = pushbackReader.read()

            if (code >= 0) {
                charNumber++
            }

            return if (code < 0) { 0 } else { code }
        }
    }

    fun readChar() = read().toChar()

    fun unread(code: Int) = pushbackReader.unread(code)
    fun unread(char: Char) = pushbackReader.unread(char.code)
}
