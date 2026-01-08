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
    }
}

