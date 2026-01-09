import moe.karla.maven.publishing.MavenPublishingExtension

plugins {
    base
//    id("moe.karla.maven-publishing")
    alias(libs.plugins.maven.publish)
}

repositories {
    mavenCentral()
}

mavenPublishing {
//  GOTO: build-logic/src/main/java/buildlogic/PublishingSetupPlugin.java
    manuallyPomSetup = true
    publishingType = MavenPublishingExtension.PublishingType.USER_MANAGED
}

tasks.register("publishAllPublicationsToRootStageRepository") {
    dependsOn(
        gradle.includedBuilds
            .filter { it.name != "local-verifier" }
            .map { it.task(":publishAllPublicationsToRootStageRepository") }
    )
}

