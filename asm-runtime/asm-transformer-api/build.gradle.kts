plugins {
    java
    `java-library`
    `asm-publishing`
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    api(libs.asm)
    api(libs.asm.commons)
    api(libs.asm.util)
    api(libs.asm.tree)
}

