plugins {
    `java-library`
    kotlin("jvm")
}

// This library has no dependencies of its own for now.
dependencies {
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
}
