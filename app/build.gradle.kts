project.description = "Implementation of the calculator project"
project.group = "com.sunman24"
project.version = "1.0"
project.status = "completed"


plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("org.jetbrains.dokka") version "1.7.10"
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("ch.obermuhlner:big-math:2.3.0")
    implementation("ch.obermuhlner:kotlin-big-math:2.3.0")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.10")
}

sourceSets {
    main {
        java.srcDirs("src/main")
        resources.exclude("*")
    }
}

application {
    mainClass.set("calculator.AppKt")
}


tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.7"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.7"
    }
}

tasks.test {
    useJUnit()
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("calculator")

    manifest {
        attributes(mapOf(
            "Main-Class" to "calculator.AppKt",
            "Implementation-Title" to "calculator",
            "Implementation-Version" to "1.0",
        ))
    }
}

tasks.distZip { archiveBaseName.set("calculator") }
tasks.distTar { archiveBaseName.set("calculator") }
