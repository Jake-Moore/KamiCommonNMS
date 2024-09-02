@file:Suppress("UNCHECKED_CAST")

plugins {
    // Unique plugins for this module
    id("com.gradleup.shadow")
}

dependencies {
    // Shade :api code into the core
    implementation(project(":api"))

    // Common Dependencies (compileOnly to avoid shading)
    (rootProject.extra["commonDependencies"] as List<String>).forEach(dependencies::shadow)

    // standalone-utils from KamiCommon
    compileOnly(project.property("standaloneUtils") as String)

    implementation(project(":versions:v1_8_R1"))
    implementation(project(":versions:v1_8_R2"))
    implementation(project(":versions:v1_8_R3"))
    implementation(project(":versions:v1_9_R1"))
    implementation(project(":versions:v1_9_R2"))
    implementation(project(":versions:v1_10_R1"))
    implementation(project(":versions:v1_11_R1"))
    implementation(project(":versions:v1_12_R1"))
    implementation(project(":versions:v1_13_R1"))
    implementation(project(":versions:v1_13_R2"))
    implementation(project(":versions:v1_14_R1"))
    implementation(project(":versions:v1_15_R1"))
    implementation(project(":versions:v1_16_R1"))
    implementation(project(":versions:v1_16_R2"))
    implementation(project(":versions:v1_16_R3"))
    // These are compileOnly so that we can include the reobfJar outputs
    compileOnly(project(":versions:v1_17_R1"))
    compileOnly(project(":versions:v1_18_R1"))
    compileOnly(project(":versions:v1_18_R2"))
    compileOnly(project(":versions:v1_19_R1"))
    compileOnly(project(":versions:v1_19_R2"))
    compileOnly(project(":versions:v1_19_R3"))
    compileOnly(project(":versions:v1_20_R1"))
    compileOnly(project(":versions:v1_20_R2"))
    compileOnly(project(":versions:v1_20_R3"))

    // Starting with 1_20_CB we can opt to not re-obf, so we can shade again
    implementation(project(":versions:v1_20_CB"))
    implementation(project(":versions:v1_21_R1"))

    implementation(project(":versions:worlds6"))
    implementation(project(":versions:worlds7"))

    // So we have access to the Clipboard class
    compileOnly("com.sk89q.worldedit:bukkit:6.1.9")

    compileOnly(project.property("lowestSpigotDep") as String)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set("")
        // configurations = listOf(project.configurations.shadow.get())
    }
}

// Use this to load shadowJar outputs after project evaluation
// This ensures the userdev reobfJar tasks are present before we use them
gradle.projectsEvaluated {
    tasks.getByName("publishShadowPublicationToMavenRepository").dependsOn(tasks.jar)

    tasks.shadowJar {
        // Add the 1.17 to 1.20R3 reobf outputs
        from(project(":versions:v1_17_R1").tasks.getByName("reobfJar").outputs)
        from(project(":versions:v1_18_R1").tasks.getByName("reobfJar").outputs)
        from(project(":versions:v1_18_R2").tasks.getByName("reobfJar").outputs)
        from(project(":versions:v1_19_R1").tasks.getByName("reobfJar").outputs)
        from(project(":versions:v1_19_R2").tasks.getByName("reobfJar").outputs)
        from(project(":versions:v1_19_R3").tasks.getByName("reobfJar").outputs)
        from(project(":versions:v1_20_R1").tasks.getByName("reobfJar").outputs)
        from(project(":versions:v1_20_R2").tasks.getByName("reobfJar").outputs)
        from(project(":versions:v1_20_R3").tasks.getByName("reobfJar").outputs)
    }
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            groupId = rootProject.group.toString()
            artifactId = "spigot-nms"
            version = rootProject.version.toString()
            // Use shadow component so that only the shadow() components are added to the publication pom
            project.extensions.getByType<com.github.jengelman.gradle.plugins.shadow.ShadowExtension>().component(this)

            // Modify the commonDependencies to use compile scope (transitive dependencies)
            pom.withXml {
                asNode().apply {
                    // Find dependencies and modify their scope
                    val dependenciesNode = ((get("dependencies") as groovy.util.NodeList).firstOrNull()
                        ?: appendNode("dependencies")) as groovy.util.Node

                    // List of common dependencies from rootProject.extra
                    val commonDependencies = rootProject.extra["commonDependencies"] as List<String>

                    // Iterate over dependency nodes and modify their scope
                    dependenciesNode.children().forEach { c ->
                        val dependencyNode = c as groovy.util.Node
                        val artifactIdNode = (dependencyNode.get("artifactId") as? groovy.util.NodeList)?.firstOrNull() as? groovy.util.Node
                        val scopeNode = (dependencyNode.get("scope") as? groovy.util.NodeList)?.firstOrNull() as? groovy.util.Node

                        if (artifactIdNode?.text() in commonDependencies.map { it.split(":")[1] }) {
                            scopeNode?.setValue("compile")
                        }
                    }
                }
            }
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
