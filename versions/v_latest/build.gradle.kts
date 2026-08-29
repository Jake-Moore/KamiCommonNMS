plugins {
    // Unique plugins for this module
    id("io.papermc.paperweight.userdev") // 1. add the Paperweight plugin
}

dependencies {
    // Unique dependencies for this module
    // Confirmed working for 1.21.5, 1.21.8, 1.21.9 & 1.21.10
    paperweight.paperDevBundle(rootProject.property("highestPaperDep") as String) // 2. add the dev bundle (contains all apis)

    compileOnly(project(":versions:v1_13_R1"))
    compileOnly(project(":versions:v1_14_R1"))
}

// Starting with 1.20.5 Paper we can choose not to reobf the jar, leaving it mojang mapped
//  we forfeit spigot compatibility, but it will natively work on paper
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

java {
    // paper-api 26.2's class files are major 69, which JDK 21's javac cannot read at all.
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}
configurations.named("compileClasspath").configure {
    // paper-api 26.2's Gradle metadata declares org.gradle.jvm.version=25, and the resolver
    //  rejects it for a Java 21 consumer BEFORE javac runs - so options.release cannot reach this.
    attributes {
        attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}
tasks.withType<JavaCompile>().configureEach {
    // The output must stay Java 21. A jar mixing class-file versions does not work: the JVM loads
    //  referenced classes during VERIFICATION, not lazily at first use, so a single major-69 class
    //  here would make every provider in :core that merely names it fail to load on a pre-26 JVM.
    options.release.set(21)
}
