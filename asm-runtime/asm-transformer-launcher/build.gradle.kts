plugins {
    java
    `asm-publishing`
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(project(":asm-transformer-runtime"))
}