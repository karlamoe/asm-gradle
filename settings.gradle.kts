val builds = mutableListOf<String>()

fun subbuild(path: String) {
    builds.add(path)
    includeBuild(path)
}

subbuild("asm-gradle-plugin")
subbuild("asm-runtime")

if (rootDir.resolve("local-verifier").isDirectory) {
    subbuild("local-verifier")
}


builds.forEach { build ->
    rootDir.resolve("gradle.properties").copyTo(
        rootDir.resolve(build).resolve("gradle.properties"),
        overwrite = true,
    )
    println("Copied gradle.properties for $build")
}
