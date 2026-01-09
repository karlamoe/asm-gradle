//mavenPublishing {
//  GOTO: build-logic/src/main/java/buildlogic/PublishingSetupPlugin.java
//}

tasks.register("publishAllPublicationsToRootStageRepository") {
    dependsOn(
        gradle.includedBuilds
            .filter { it.name != "local-verifier" }
            .map { it.task(":publishAllPublicationsToRootStageRepository") }
    )
}

