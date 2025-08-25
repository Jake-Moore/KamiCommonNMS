@file:Suppress("UNCHECKED_CAST")

val reobfVersions = listOf(
    "v1_17_R1",
    "v1_18_R1",
    "v1_18_R2",
    "v1_19_R1",
    "v1_19_R2",
    "v1_19_R3",
    "v1_20_R1",
    "v1_20_R2",
    "v1_20_R3",
)

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
}

allprojects {
    dependencies {
        // Lombok
        compileOnly(project.property("lombokDep") as String)
        annotationProcessor(project.property("lombokDep") as String)
        testImplementation(project.property("lombokDep") as String)
        testAnnotationProcessor(project.property("lombokDep") as String)

        // IntelliJ annotations
        compileOnly(project.property("jetbrainsDep") as String)
    }
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

// Requires J21 since some submodules (which are shaded) are Java 21
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn(shadowJar)
    shadowJar.get().dependsOn(jar)
    shadowJar {
        archiveClassifier.set("")
        // configurations = listOf(project.configurations.shadow.get())
    }
}

// Javadoc module detection requires project evaluation (so api module is detected)
gradle.projectsEvaluated {
    // -------------------------------------------------- //
    //                      Javadocs                      //
    // -------------------------------------------------- //
    // Take api, core
    //   The version specific implementation modules don't have public API or javadocs
    //   They are excluded to avoid Javadoc errors due to NMS references that javadoc can't handle
    val exportedProjects = listOf(
        project(":api"),
        project(":core"),
    )

    val aggregateJavadoc = tasks.register<Javadoc>("aggregateJavadoc") {
        val javaProjects = exportedProjects.filter { project ->
            project.plugins.hasPlugin("java")
        }

        // println("Generating Javadocs for projects (${javaProjects.size}): ${javaProjects.map { it.path }}")

        source(javaProjects.map { proj ->
            proj.extensions.getByType<SourceSetContainer>()["main"].allJava.matching {
                // Exclude classes that Javadoc can't handle, and that aren't needed in the docs
                exclude("**/WorldEdit6.java")
                exclude("**/WorldGuard6.java")
                exclude("**/WorldEdit7.java")
                exclude("**/WorldGuard7.java")
            }
        })
        classpath = files(javaProjects.map {
            it.extensions.getByType<SourceSetContainer>()["main"].compileClasspath
        })

        destinationDir = file("${layout.buildDirectory.get().asFile.absolutePath}/docs/aggregateJavadoc")

        (options as StandardJavadocDocletOptions).apply {
            encoding = "UTF-8"
            charSet = "UTF-8"

            // Set window title and doc title
            windowTitle = "KamiCommonNMS"
            docTitle = "KamiCommonNMS ${rootProject.version} API"
        }
    }

    // Create the Javadoc JAR task (provides rich javadocs in IDEs)
    val aggregateJavadocJar = tasks.register<Jar>("aggregateJavadocJar") {
        group = "documentation"
        description = "Assembles a JAR archive containing the combined Javadocs"

        archiveClassifier.set("javadoc")
        from(aggregateJavadoc.get().destinationDir)

        dependsOn(aggregateJavadoc)
    }

    // Create the combined sources JAR (contains .java files) (provides fallback sources in IDEs)
    val aggregateSourcesJar = tasks.register<Jar>("aggregateSourcesJar") {
        group = "build"
        description = "Assembles sources JAR for all modules"

        val javaProjects = exportedProjects.filter {
            it.plugins.hasPlugin("java")
        }

        from(javaProjects.map {
            it.extensions.getByType<SourceSetContainer>()["main"].allSource
        })
        archiveClassifier.set("sources")
    }





    // -------------------------------------------------- //
    //                     publishing                     //
    // -------------------------------------------------- //
    tasks.publish.get().dependsOn(aggregateJavadocJar)
    tasks.publish.get().dependsOn(aggregateSourcesJar)
    publishing {
        publications {
            create<MavenPublication>("shadow") {
                groupId = rootProject.group.toString()
                artifactId = "spigot-nms"
                version = rootProject.version.toString()
                // Use shadow component so that only the shadow() components are added to the publication pom
                from(components["shadow"])
                // Add both documentation artifacts
                artifact(tasks.named("aggregateJavadocJar")) // HTML documentation
                artifact(tasks.named("aggregateSourcesJar")) // Java source files

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

    // load shadowJar outputs after project evaluation
    // This ensures the userdev reobfJar tasks are present before we use them
    tasks.getByName("publishShadowPublicationToMavenRepository").dependsOn(tasks.jar)

    tasks.shadowJar {
        // Ensure reobfJar tasks run before shadowJar
        dependsOn(reobfVersions.map { ":versions:$it:reobfJar" })

        // Add the 1.17 to 1.20R3 reobf outputs
        reobfVersions.forEach { version ->
            val task = project(":versions:$version").tasks.getByName("reobfJar")
            from(zipTree(task.outputs.files.singleFile))
        }
    }
}