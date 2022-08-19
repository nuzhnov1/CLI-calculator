## Description
The implementation of the calculator project is one of several  
projects in the Kotlin Basics track at the [Jetbrains Academy].

[Jetbrains Academy]: https://hyperskill.org/study-plan

## Prerequisites

JRE 1.8 or higher

## Repository organization
The "master" branch contains a calculator based on java.math.BigInteger.  

The "big-decimal" branch contains a calculator based on  
java.math.BigDecimal, which also supports the following features:
* built-in functions (trigonometric functions, hyperbolic functions etc.);
* improved lexis;
* syntax of the implicit multiplication;

## Building and running
1. If you want to build a BigDecimal calculator, first switch to the  
"big-decimal" branch.

2. Clone this repository or download it. If you downloaded it, then unzip it.

3. Move to the project root directory in your command shell:

```shell
cd <project root directory>
```

4. Run the following command:  

On Unix-like operating systems:
```shell
./gradlew build
```
On Windows:
```batch
./gradlew.bat build
```

5. Unpack the tar or zip archive located in the  
\<project root directory\>/app/build/distributions

6. Move to the \<archive directory\>/bin directory in your command shell:  

```shell
cd <archive directory>/bin
```

7. Run the calculator application:

On Unix-like operating systems:
```shell
./app
```

On Windows:
```batch
./app.bat
```
