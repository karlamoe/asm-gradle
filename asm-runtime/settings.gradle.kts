pluginManagement {
    includeBuild("../build-logic")
}
plugins {
    id("build-logic")
}

include("asm-transformer-api")
include("asm-transformer-runtime")
include("asm-transformer-launcher")

