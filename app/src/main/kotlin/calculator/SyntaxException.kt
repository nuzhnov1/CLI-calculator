package calculator

import java.io.IOException

class SyntaxException : IOException {
    constructor()
    constructor(message: String): super(message)
    constructor(cause: Throwable): super(cause)
    constructor(message: String, cause: Throwable): super(message, cause)
}
