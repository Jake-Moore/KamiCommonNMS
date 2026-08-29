plugins {
    // Unique plugins for this module
    id("io.papermc.paperweight.userdev") // 1. add the Paperweight plugin
}

dependencies {
    // Unique dependencies for this module
    // Confirmed working for 1.21.5 through 1.21.11, see v_latest for 26.1+
    //  ChunkPos became a record in the 26.1 line, so its x/z fields are accessors there.
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT") // 2. add the dev bundle (contains all apis)

    compileOnly(project(":versions:v1_13_R1"))
    compileOnly(project(":versions:v1_14_R1"))
}

// Starting with 1.20.5 Paper we can choose not to reobf the jar, leaving it mojang mapped
//  we forfeit spigot compatibility, but it will natively work on paper
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
