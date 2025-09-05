plugins {
    // Unique plugins for this module
    id("io.papermc.paperweight.userdev") // 1. add the Paperweight plugin
}

dependencies {
    // Unique dependencies for this module
    // Confirmed working for 1.21.4, see 1_21_CB for 1.21.5+
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT") // 2. add the dev bundle (contains all apis)

    compileOnly(project(":versions:v1_13_R1"))
    compileOnly(project(":versions:v1_14_R1"))
}

// Starting with 1.20.5 Paper we can choose not to reobf the jar, leaving it mojang mapped
//  we forfeit spigot compatibility, but it will natively work on paper
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION