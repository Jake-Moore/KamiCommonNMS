import java.util.zip.ZipFile

// The shadow component takes org.gradle.jvm.version from the toolchain, and neither
// options.release nor targetCompatibility reaches it. Pin it so the metadata matches the bytecode.
// Only takes effect inside afterEvaluate, because the shadow plugin writes the attribute after us.
afterEvaluate {
    configurations.named("shadowRuntimeElements").configure {
        attributes {
            attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
        }
    }
}

// KamiCommon runs on servers from 1.8.x upward, so nothing it ships may need a JVM newer than 21,
// not even the classes built against Paper 26.2. Two ways to break that silently: the JVM resolves
// referenced classes during verification rather than lazily, so ONE major-69 class stops every
// provider that names it from loading; and a wrong jvm.version in the published metadata locks out
// consumers while the bytecode looks fine.
val verifyJava21Compatibility = tasks.register("verifyJava21Compatibility") {
    group = "verification"
    description = "Fails if the shaded jar, or its published metadata, would lock out Java 21 servers."
    dependsOn(tasks.named("shadowJar"), tasks.named("generateMetadataFileForShadowPublication"))

    val jarFile = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }
    val classifier = tasks.named<Jar>("shadowJar").flatMap { it.archiveClassifier }
    val moduleFile = layout.buildDirectory.file("publications/shadow/module.json")

    doLast {
        // The jar inspected below must be the one consumers receive. If shadowJar grows a
        // classifier, the shaded jar publishes as -all and the primary artifact becomes the thin
        // jar, with none of the version modules in it. Everything else here would still pass.
        if (classifier.get().isNotEmpty()) {
            throw GradleException(
                "shadowJar has classifier '${classifier.get()}', so the shaded jar is not the primary " +
                        "published artifact. Consumers would receive the thin jar."
            )
        }

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
        // A walker that matches nothing would pass forever.
        if (inspected < 1000) {
            throw GradleException(
                "verifyJava21Compatibility only inspected $inspected classes in ${jarFile.get().asFile.name}, " +
                        "far below the expected count, so it is not actually reading the artifact."
            )
        }
        if (highest > 65) {
            throw GradleException(
                "$worst is class-file major $highest (Java ${highest - 44}). Nothing here may exceed major 65 " +
                        "(Java 21): the JVM resolves referenced classes during verification, so this breaks every " +
                        "server below 26.x, not only those that use the class."
            )
        }

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
