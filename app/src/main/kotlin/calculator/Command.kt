package calculator

/**
 * Sealed interface of calculator commands:
 * 1. Help command;
 * 2. Show functions command;
 * 3. Exit command;
 */
sealed interface Command : CalculationResult {
    object HELP : Command {
        override fun toString() =
            """
            |The simple scientific calculator.
            |Supported operators:
            |   1) Addition (+) and subtraction (-);
            |   2) Multiplication (*) and division (/);
            |   3) Unary plus (+) and unary minus (-);
            |   4) Exponentiation (^);
            |   5) Factorial (!) and percent (%);
            |The calculator also supports an implicit multiplication operator, for example, the expression "1a"
            |is equivalent to "1*a", but "a1" is not equivalent to "a*1", in this case "a1" is the only identifier.
            |Parentheses and square brackets can be used to change the order of operations in expressions.
            |The calculator has the set of built-in functions. To see all functions enter the following command:
            |   \functions
            |You can declare variables using the following syntax:
            |   <identifier> = <expression>
            |Identifiers are case-sensitive. The constants pi and e (Euler number) are defined by default.
            |Reassign of these constants is forbidden.
            """.trimMargin().trimIndent()
    }

    object FUNCTIONS : Command {
        override fun toString() =
            """
            |List of built-in calculator functions.
            |Power functions:
            |   sqrt(x) - evaluates the square root of x;
            |   cbrt(x) - evaluates the cube root of x;
            |   root(x, n) - evaluates the nth-root of x;
            |   exp(x) - evaluates e^x, where e is the Euler number;
            |Logarithmic functions:
            |   ln(x) - evaluates the natural logarithm (logarithm with the base of the e number);
            |   log(b, x) - evaluates the logarithm of x with base n;
            |   log2(x) - evaluates the logarithm of x with base 2;
            |   log10(x) - evaluates the logarithm of x with base 10;
            |Trigonometric functions:
            |   sin(x) - evaluates the sine of x;
            |   cos(x) - evaluates the cosine of x;
            |   tan(x) - evaluates the tangent of x;
            |   cot(x) - evaluates the cotangent of x;
            |Inverse trigonometric functions:
            |   asin(x) - evaluates the arcsine of x (inverse function for sine);
            |   acos(x) - evaluates the arccosine of x (inverse function for cosine);
            |   atan(x) - evaluates the arctangent of x (inverse function for tangent);
            |   acot(x) - evaluates the arccotangent of x (inverse function for cotangent);
            |Hyperbolic functions:
            |   sinh(x) - evaluates the hyperbolic sine of x;
            |   cosh(x) - evaluates the hyperbolic cosine of x;
            |   tanh(x) - evaluates the hyperbolic tangent of x;
            |   coth(x) - evaluates the hyperbolic cotangent of x;
            |Inverse hyperbolic functions:
            |   asinh(x) - evaluates the area hyperbolic sine of x (inverse function for hyperbolic sine);
            |   acosh(x) - evaluates the area hyperbolic cosine of x (inverse function for hyperbolic cosine);
            |   atanh(x) - evaluates the area hyperbolic tangent of x (inverse function for hyperbolic tangent);
            |   acoth(x) - evaluates the area hyperbolic cotangent of x (inverse function for hyperbolic cotangent);
            |Rounding functions:
            |   ceil(x) - evaluates the smallest integer that greater than or equal to x;
            |   floor(x) - evaluates the greater integer that less than or equal to x;
            |   round(x) - evaluates the rounded value of x;
            |Other functions:
            |   abs(x) - evaluates the absolute value of x;
            |   fraction(x) - evaluates the fractional value of x;
            |   hypot(x, y) - evaluates the hypotenuse for the x and y legs;
            """.trimMargin().trimIndent()
    }

    object EXIT : Command {
        override fun toString() = "exit"
    }
}
