plugins {
    java
    `java-library`
}

dependencies {
    api(project(":asm-transformer-api"))
    api(libs.asm.util)
}