project.description = """
    |Implementation of the calculator project - one of several Kotlin Basics track projects
    |at JetBrains Academy
""".trimMargin().trimIndent()

project.group = "com.sunman24"
project.status = "in development"
project.version = "0.1"


plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    application
}


repositories {
    mavenCentral()
}


dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


application {
    mainClass.set("Calculator.AppKt")
}


tasks.test {
    useJUnit()
    useJUnitPlatform()
}
