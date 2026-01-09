plugins {
    java
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("buildLogic") {
            id = "build-logic"
            implementationClass = "buildlogic.BuildLogic"
        }
        create("asmPublish") {
            id = "asm-publishing"
            implementationClass = "buildlogic.DependencyPublishingPlugin"
        }
    }
}

fun DependencyHandler.plugin(signing: Provider<PluginDependency>): Any {
    val id = signing.get().pluginId
    val version = signing.get().version
    return create("$id:$id.gradle.plugin:$version")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(plugin(libs.plugins.gpg.signing))
    implementation(plugin(libs.plugins.maven.publish))
}


