plugins {
    // Unique plugins for this module
    id("io.papermc.paperweight.userdev") // 1. add the Paperweight plugin
}

dependencies {
    // Unique dependencies for this module
    // Confirmed working for 1.21.5, 1.21.8, 1.21.9 & 1.21.10
    paperweight.paperDevBundle(rootProject.property("highestPaperDep") as String) // 2. add the dev bundle (contains all apis)

    // ModernVersionedComponent lives in v1_18_R2 now: the native Adventure API it exposes
    // arrived in 1.18.2, so that is the lowest floor it can sit at, and the logger adapter
    // dispatched from 1.18.2 has to be able to see it.
    compileOnly(project(":versions:v1_18_R2"))
    compileOnly(project(":versions:v1_13_R1"))
    compileOnly(project(":versions:v1_14_R1"))
}

// Starting with 1.20.5 Paper we can choose not to reobf the jar, leaving it mojang mapped
//  we forfeit spigot compatibility, but it will natively work on paper
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

// Toolchain 25 and target 21 now come from the floor table in versions/build.gradle.kts.
