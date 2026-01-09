plugins {
    java
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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


