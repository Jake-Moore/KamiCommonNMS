import java.util.zip.ZipFile

// spigot-nms-text declares org.gradle.jvm.version = 8, which is a promise about bytecode that this
// module does not produce. Every class in it comes from Adventure. So the promise has to be checked
// against the artifact, or an Adventure release that raises its own bytecode would ship here under a
// floor of 8 and break every Java 8 server that resolved it.
val verifyTextFloor = tasks.register("verifyTextFloor") {
    group = "verification"
    description = "Fails if the shaded Adventure jar exceeds Java 8, or if its metadata stops saying 8."
    dependsOn(tasks.named("shadowJar"), tasks.named("generateMetadataFileForShadowPublication"))

    val jarFile = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }
    val moduleFile = layout.buildDirectory.file("publications/shadow/module.json")

    doLast {
        var inspected = 0
        var highest = 0
        var worst = ""
        ZipFile(jarFile.get().asFile).use { zip ->
            zip.entries().asSequence()
                .filter { it.name.endsWith(".class") }
                // Multi-release entries are governed by their own version directory. A Java 8 JVM
                // never reads them, so they are not a violation of its floor.
                .filterNot { it.name.startsWith("META-INF/versions/") }
                .forEach { entry ->
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
        // A walker that matches nothing passes forever.
        if (inspected < 500) {
            throw GradleException(
                "verifyTextFloor only inspected $inspected classes in ${jarFile.get().asFile.name}, " +
                        "far below the expected count, so it is not reading the artifact."
            )
        }
        if (highest > 52) {
            throw GradleException(
                "$worst is class-file major $highest (Java ${highest - 44}), above this module's declared " +
                        "floor of Java 8. An Adventure upgrade raised the bytecode. Either lower it or raise " +
                        "the declared org.gradle.jvm.version to match, but do not ship the two disagreeing."
            )
        }
        val module = moduleFile.get().asFile
        if (!module.exists()) { throw GradleException("expected Gradle module metadata at $module") }
        val declared = Regex("\"org\\.gradle\\.jvm\\.version\"\\s*:\\s*(\\d+)").find(module.readText())
            ?: throw GradleException("no org.gradle.jvm.version found in $module")
        if (declared.groupValues[1] != "8") {
            throw GradleException(
                "published metadata declares org.gradle.jvm.version=${declared.groupValues[1]}, expected 8. " +
                        "The bytecode may be fine, but every consumer below that will fail to resolve this module."
            )
        }
        logger.lifecycle("verifyTextFloor: $inspected classes, highest major $highest, metadata declares 8")
    }
}
tasks.named("build") { dependsOn(verifyTextFloor) }
tasks.named("publish") { dependsOn(verifyTextFloor) }
