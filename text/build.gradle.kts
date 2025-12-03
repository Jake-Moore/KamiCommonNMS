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
val TEXT_VERSION = "1.0.5"
version = TEXT_VERSION

val adventureVersion = "4.25.0"
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