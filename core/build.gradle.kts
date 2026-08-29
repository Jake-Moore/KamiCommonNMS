@file:Suppress("UNCHECKED_CAST")

import java.util.zip.ZipFile

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
    // Shade our adventure relocated api into core
    implementation(project.property("adventureDep") as String)

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
    implementation(project(":versions:v1_21_4"))
    implementation(project(":versions:v1_21_9"))
    implementation(project(":versions:v1_21_11"))
    implementation(project(":versions:v_latest"))

    implementation(project(":versions:worlds6"))
    implementation(project(":versions:worlds7"))

    // So we have access to the Clipboard class
    compileOnly("com.sk89q.worldedit:bukkit:6.1.9")

    compileOnly(project.property("serverAPI") as String)
}

// Under MC_SERVER_NEWEST_API=true, `serverAPI` is paper-api 26.2, whose Gradle
// metadata declares org.gradle.jvm.version=25. Ask the resolver for a 25-compatible
// library, run javac on JDK 25 so it can read class-file major 69, but keep EMITTING
// Java 21 bytecode - the providers here must load on pre-26 JVMs, and the JVM loads
// referenced classes during verification rather than lazily.
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    // Declare the Java 21 target explicitly. options.release below controls what javac EMITS,
    //  but it does not feed org.gradle.jvm.version on the outgoing variants - those default to
    //  the toolchain, which would publish metadata claiming Java 25 over major-65 bytecode and
    //  make every Java 21 consumer unable to resolve this module.
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
configurations.named("compileClasspath").configure {
    attributes {
        attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}
tasks.withType<JavaCompile>().configureEach { options.release.set(21) }

// The shadow component derives org.gradle.jvm.version from the toolchain, and neither
//  options.release nor targetCompatibility reaches it. Pin it, so the metadata matches the
//  bytecode (major 65) no matter which JDK ran the build. Without this the published module
//  claims Java 25 and every Java 21 consumer fails to resolve it.
afterEvaluate {
    configurations.named("shadowRuntimeElements").configure {
        attributes {
            attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
        }
    }
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

// -------------------------------------------------- //
//        Java 21 compatibility, enforced not assumed   //
// -------------------------------------------------- //
// KamiCommon runs on servers from 1.8.x upward, so nothing it ships may require a JVM newer
//  than 21 - not even the classes compiled against Paper 26.2. This is easy to break silently:
//  the JVM loads referenced classes during verification rather than lazily, so ONE major-69
//  class makes every provider that merely names it unloadable, and a wrong jvm.version in the
//  metadata locks out consumers while the bytecode looks fine.
val verifyJava21Compatibility = tasks.register("verifyJava21Compatibility") {
    group = "verification"
    description = "Fails if the shaded jar, or its published metadata, would lock out Java 21 servers."
    dependsOn(tasks.shadowJar, tasks.named("generateMetadataFileForShadowPublication"))

    val jarFile = tasks.shadowJar.flatMap { it.archiveFile }
    val moduleFile = layout.buildDirectory.file("publications/shadow/module.json")

    doLast {
        // 1. No class in the artifact may exceed major 65 (Java 21).
        var inspected = 0
        var highest = 0
        var worst = ""
        ZipFile(jarFile.get().asFile).use { zip ->
            zip.entries().asSequence().filter { it.name.endsWith(".class") }.forEach { entry ->
                zip.getInputStream(entry).use { stream ->
                    val header = stream.readNBytes(8)
                    if (header.size == 8 && header[0] == 0xCA.toByte() && header[1] == 0xFE.toByte()) {
                        val major = ((header[6].toInt() and 0xFF) shl 8) or (header[7].toInt() and 0xFF)
                        inspected++
                        if (major > highest) { highest = major; worst = entry.name }
                    }
                }
            }
        }
        // Guard the guard: a walker that matches nothing would pass forever.
        if (inspected < 1000) {
            throw GradleException(
                "verifyJava21Compatibility only inspected $inspected classes in ${jarFile.get().asFile.name}. " +
                        "That is far below the expected count, so this check is not actually looking at the artifact."
            )
        }
        if (highest > 65) {
            throw GradleException(
                "$worst is class-file major $highest (Java ${highest - 44}). Nothing in this artifact may " +
                        "exceed major 65 (Java 21) - the JVM resolves referenced classes during verification, " +
                        "so this would break every server below 26.x, not just those that use the class."
            )
        }

        // 2. The published metadata must declare Java 21, or consumers cannot resolve us.
        val module = moduleFile.get().asFile
        if (!module.exists()) { throw GradleException("expected Gradle module metadata at $module") }
        val declared = Regex("\"org\\.gradle\\.jvm\\.version\"\\s*:\\s*(\\d+)").find(module.readText())
            ?: throw GradleException("no org.gradle.jvm.version found in $module")
        if (declared.groupValues[1] != "21") {
            throw GradleException(
                "published metadata declares org.gradle.jvm.version=${declared.groupValues[1]}, expected 21. " +
                        "The bytecode may be fine, but every Java 21 consumer will fail to resolve this module."
            )
        }

        logger.lifecycle("verifyJava21Compatibility: $inspected classes, highest major $highest, metadata declares 21")
    }
}
tasks.named("build") { dependsOn(verifyJava21Compatibility) }
tasks.named("publish") { dependsOn(verifyJava21Compatibility) }

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
    }
}