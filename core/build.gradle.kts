@file:Suppress("UNCHECKED_CAST")

// Modules shaded from their plain jar. Kept beside reobfVersions because between them they must
// account for every entry in settings.gradle.kts. verifyFloors fails if a module has no compiled
// output, and verifyNmsBundles fails if its adapter is missing from the shaded jar.
val plainVersions = listOf(
    "v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1", "v1_9_R2", "v1_10_R1", "v1_11_R1", "v1_12_R1",
    "v1_13_R1", "v1_13_R2", "v1_14_R1", "v1_15_R1", "v1_16_R1", "v1_16_R2", "v1_16_R3",
    "v1_20_CB", "v1_21_4", "v1_21_9", "v1_21_11", "v_latest",
    "worlds6", "worlds7",
)

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

// Resolves :text's relocated jar as a FILE, for embedding rather than shading.
val textShim: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    // Shade :api code into the core
    implementation(project(":api"))
    // :text-impl, NOT :text. :text is the relocated Adventure alone; :text-impl merges it with the
    // classes that use it. Embedding :text shipped a nested jar with 839 Adventure classes and zero
    // implementations, so TextBundles.forModule would have thrown ClassNotFoundException on every
    // server below 1.21.4. All six build checks passed on that jar.
    textShim(project(":text-impl"))
    // :text is compiled against but not shaded in as loose classes. It is embedded as a NESTED JAR
    // instead; see the shadowJar block.
    compileOnly(project(project.property("adventureDep") as String))


    // Common Dependencies (compileOnly to avoid shading)
    (rootProject.extra["commonDependencies"] as List<String>).forEach(dependencies::shadow)

    // standalone-utils from KamiCommon
    compileOnly(project.property("standaloneUtils") as String)


    // The versions/* modules are deliberately absent here. :core resolves them by name at runtime
    // (see NmsBundles), so it must not compile against them, and it cannot. They now target the
    // JVM their own Minecraft version required, and a Java 8 consumer cannot resolve a Java 21
    // producer. They are bundled into the shaded jar below, from their jar task outputs.

    // So we have access to the Clipboard class
    compileOnly("com.sk89q.worldedit:bukkit:6.1.9")

    compileOnly(project.property("serverAPI") as String)
}

// A 1.8.8 server loads this module in full, so it sits at the same floor as :api.
extra["moduleFloor"] = 8
apply(from = "$rootDir/gradle/module-floor.gradle.kts")

tasks {
    publish.get().dependsOn(build)
    build.get().dependsOn(shadowJar)
    shadowJar.get().dependsOn(jar)
    shadowJar {
        archiveClassifier.set("")
        // configurations = listOf(project.configurations.shadow.get())

        // :text is not shaded in as loose classes. It is the relocated Adventure, and it exists only
        // for servers with no native Adventure, meaning everything below 1.21.4.
        //
        // Shading it flat put the bytes in the jar as ordinary class entries, and javac has no notion
        // of an internal package, so every consumer could import
        // com.kamikazejam.kamicommon.nms.text.kyori.* and some would. Dependency scoping cannot fix
        // that: measured 2026-08-30, a spigot-jar consumer compiled against the shaded Adventure
        // successfully even with the dependency declared runtime-only, because scope metadata cannot
        // hide bytes that are physically present.
        //
        // It ships as a JAR INSIDE THIS JAR instead, at internal-libs/adventure.jar, and is loaded at
        // runtime through a child classloader. Java's classpath has no nested-jar support, so those
        // classes are not classpath entries at all. That survives being shaded by a consumer, which
        // is the property scoping could not provide: shadow copies the nested jar through
        // byte-identically, measured at two levels of shading.
        dependencies {
            exclude(project(":text"))
        }
        // The nested jar. Resolved through a configuration rather than a cross-project task lookup,
        // which fails at configuration time. :text already makes its shadow jar the default outgoing
        // artifact, so project(":text") resolves to the relocated jar.
        //
        // from(...) { into(...) } copies it as a FILE. It is never unpacked, which is the entire
        // point: nested jar entries are not classpath entries, so javac cannot see them.
        from(textShim) {
            into("internal-libs")
            rename { "adventure.jar" }
        }
    }
}

apply(from = "$rootDir/gradle/verify-floors.gradle.kts")

tasks.register("printServerAPI") {
    doFirst {
        println("Using Server API: ${project.property("serverAPI") as String}")
    }
}
tasks.compileJava.get().dependsOn(tasks.named("printServerAPI"))

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
            windowTitle = "KamiCommonNMS"
            docTitle = "KamiCommonNMS ${rootProject.version} API"

            // Apply a header documenting the NMS version this was built against
            val highestPaperDep = (rootProject.property("highestPaperDep") as String).removeSuffix("-SNAPSHOT")
            header = "NMS Verified For: 1.8-R0.1 - $highestPaperDep"

            // External links
            links(
                "https://docs.oracle.com/en/java/javase/21/docs/api/",
                // Paper API aggregated javadocs site
                "https://jd.papermc.io/paper/"
            )

            // Treat missing external links as warnings
            addBooleanOption("Xdoclint:none", true)
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

        // Everything else comes from its plain jar. These used to arrive as `implementation`
        // dependencies, which put them on :core's compile classpath. That was fine while every module
        // shared one toolchain, impossible now that each targets its own Minecraft version's JVM.
        // Taking the task output instead sidesteps dependency resolution entirely, which is correct:
        // this is a packaging relationship, not a compilation one.
        dependsOn(plainVersions.map { ":versions:$it:jar" })
        plainVersions.forEach { version ->
            val task = project(":versions:$version").tasks.getByName("jar")
            from(zipTree(task.outputs.files.singleFile))
        }
    }
}

apply(from = "$rootDir/gradle/verify-nms-bundles.gradle.kts")
apply(from = "$rootDir/gradle/verify-dispatch-floors.gradle.kts")


apply(from = "$rootDir/gradle/verify-adventure-isolation.gradle.kts")
apply(from = "$rootDir/gradle/verify-latest-twins.gradle.kts")
