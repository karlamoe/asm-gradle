plugins {
    java
    `java-library`
    `asm-publishing`
}

dependencies {
    api(project(":asm-transformer-api"))
    api(libs.asm.util)
}