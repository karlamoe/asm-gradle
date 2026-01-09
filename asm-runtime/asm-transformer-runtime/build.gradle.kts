plugins {
    java
    `java-library`
    `asm-publishing`
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    api(project(":asm-transformer-api"))
    api(libs.asm.util)
}