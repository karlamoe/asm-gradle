plugins {
    java
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

gradlePlugin {
    plugins {
        create("asmGradle") {
            id = "moe.karla.asm"
            implementationClass = "moe.karla.asm.gradle.AsmGradlePlugin"
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.processResources {
    val properties = mapOf(
        "version" to project.version,
    )
    inputs.properties(properties)

    filesMatching("**/metadata.properties") {
        expand(properties)
    }
}


