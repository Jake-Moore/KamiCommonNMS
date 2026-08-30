@file:Suppress("PropertyName", "VulnerableLibrariesLocal")

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
}

// Uses its own version independent of the root project since this is published separately
// It is also a standalone artifact that does not need to match the root project version
//   primarily for shading and creating an artifact of adventure that then gets bundled into NMS here
val TEXT_VERSION = "1.0.6"
version = TEXT_VERSION

val adventureVersion = "4.26.1"
dependencies {
    // Shade All Adventure APIs we need, used by older versions without native Adventure support
    implementation("net.kyori:adventure-api:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-gson:$adventureVersion")
    implementation("net.kyori:adventure-text-minimessage:$adventureVersion")
    val platform = "4.4.1"
    implementation("net.kyori:adventure-platform-bukkit:$platform")
    implementation("net.kyori:adventure-text-serializer-bungeecord:$platform")
}

java {
    // Pinned, or this module inherits whichever JDK runs the build and republishes at that version.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

// This module has no source of its own. It exists to relocate Adventure, and everything it shades is
// class-file major 52 or lower. It nevertheless published org.gradle.jvm.version = 21, because that
// attribute comes from the toolchain and nothing else feeds it. The bytecode was always fine; the
// metadata locked out every consumer below 21 for no reason, which is the same failure that made
// spigot-nms 1.2.20 unresolvable when it wrongly claimed 25.
//
// Only takes effect inside afterEvaluate: the shadow plugin writes this attribute after us.
afterEvaluate {
    configurations.named("shadowRuntimeElements").configure {
        attributes {
            attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }
    }
}

// Make the shadow jar this module's default outgoing artifact.
//
// Consumers can then write a plain project(":text") instead of
// project(path = ":text", configuration = "shadow"). That matters beyond tidiness: IntelliJ resolves
// an ordinary project dependency and does not resolve a configuration-qualified one, so the
// qualified form leaves every relocated Adventure symbol red in the IDE while Gradle builds fine.
//
// Clearing outgoing.variants is the load-bearing half. Without it Gradle can select the unshadowed
// `classes` variant during compilation, which for this module is empty, because it has no source of
// its own.
configurations {
    named("apiElements") {
        outgoing.artifacts.clear()
        outgoing.variants.clear()
        outgoing.artifact(tasks.named("shadowJar"))
    }
    named("runtimeElements") {
        outgoing.artifacts.clear()
        outgoing.variants.clear()
        outgoing.artifact(tasks.named("shadowJar"))
    }
}

apply(from = "$rootDir/gradle/verify-text-floor.gradle.kts")



tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn(shadowJar)
    shadowJar.get().dependsOn(jar)
    shadowJar {
        archiveClassifier.set("")

        // Relocate Adventure so that we can reference it directly and avoid classpath conflicts
        relocate("net.kyori", "com.kamikazejam.kamicommon.nms.text.kyori")
        // the gson serializer comes with a dependency on com.google.gson, so relocate that
        relocate("com.google.gson", "com.kamikazejam.kamicommon.nms.text.gson")

        // Ensure the service files that Adventure uses for providers are relocated properly
        mergeServiceFiles()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "version" to rootProject.version,
            "kyori_adventure_version" to adventureVersion,
        )
        inputs.properties(props)
        filesMatching("kamicommon-text-version.json") {
            expand(props)
        }
    }
}

// -------------------------------------------------- //
//                     publishing                     //
// -------------------------------------------------- //
publishing {
    publications {
        create<MavenPublication>("shadow") {
            groupId = rootProject.group.toString()
            artifactId = "spigot-nms-text"
            version = TEXT_VERSION
            from(components["shadow"])
        }
    }
    repositories {
        maven {
            credentials {
                username = System.getenv("LUXIOUS_NEXUS_USER")
                password = System.getenv("LUXIOUS_NEXUS_PASS")
            }
            // Select URL based on version (if it's a snapshot or not)
            url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.luxiouslabs.net/repository/maven-snapshots/")
            }else {
                uri("https://repo.luxiouslabs.net/repository/maven-releases/")
            }
        }
    }
}

// Apply a filter that requires this module's publish task to be called directly
// (It will NOT run on the root :publish command)
tasks.named("publish") {
    onlyIf {
        // Get all task arguments from the command line, flattening them into a single list
        val requestedTasks = gradle.startParameter.taskRequests.flatMap { it.args }

        // Construct the full, qualified path for this specific 'publish' task
        val thisFullTaskPath = ":${project.path}:publish"

        // The task will only run if its exact qualified path is found
        // within the tasks explicitly requested on the command line.
        // This ensures that running 'gradle :publish' from the root
        // will NOT trigger this submodule's publish task,
        // nor will running 'gradle publish' from within this submodule's directory.
        requestedTasks.contains(thisFullTaskPath)
    }
}