plugins {
    base
//    id("moe.karla.maven-publishing")
    alias(libs.plugins.maven.publish)
}

mavenPublishing {
//  GOTO: build-logic/src/main/java/buildlogic/PublishingSetupPlugin.java
    manuallyPomSetup = true
}

tasks.register("publishAllPublicationsToRootStageRepository") {
    dependsOn(
        gradle.includedBuilds
            .filter { it.name != "local-verifier" }
            .map { it.task(":publishAllPublicationsToRootStageRepository") }
    )
}

