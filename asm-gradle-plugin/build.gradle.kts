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

    implementation(libs.asm.tree)
}

gradlePlugin {
    plugins {
        create("asmGradle") {
            id = "moe.karla.asm"
            implementationClass = "moe.karla.asm.gradle.AsmGradlePlugin"
        }
        create("accessTransformer") {
            id = "moe.karla.asm.accesstransform"
            implementationClass = "moe.karla.asm.gradle.accesstransform.AccessTransformPlugin"
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


val generatedSrc = layout.buildDirectory.dir("generated/src/vendor/java")
val vendors = rootDir.resolve("../vendors")

val expandVendorSources = tasks.register<ProcessResources>("expandVendorSources") {
    into(generatedSrc)

    from(vendors.resolve("AccessTransformers/parser/src/main/java"))
    from(vendors.resolve("AccessTransformers/src/main/java"))

    val relocate = listOf(
        "net.neoforged.accesstransformer" to "moe.karla.asm.libs.accesstransformer"
    )

    eachFile {
        var result = path
        relocate.forEach { (from, to) ->
            result = result.replace(from.replace('.', '/'), to.replace('.', '/'))
        }
        path = result
    }
    filter { line ->
        var result = line
        relocate.forEach { (from, to) ->
            result = result.replace(from, to)
        }
        result
    }

    includeEmptyDirs = false
}

sourceSets.named("main") {
    java.srcDir(expandVendorSources)
}


