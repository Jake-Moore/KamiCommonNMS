import java.io.DataInputStream
import java.util.zip.ZipFile

// Replaces the fixed "nothing may exceed major 65" guard. That number was a convention living in one
// place; this is a table derived from the modules themselves and checked against the artifact.
//
//   A. Ceiling per area.   Every class in the shaded jar is within the floor of the module it came
//      from. A dependency bump that raises shaded bytecode fails here, naming the class.
//   B. No downward refs.   No class in a lower-floor area symbolically references one in a higher
//      floor. This is the check that matters: every floor can be correct while a single
//      `new Teleporter_LATEST()` re-couples them and takes out every server below 26.x.
//   C. Metadata matches.   org.gradle.jvm.version against the bytecode. A correct artifact that
//      nobody can resolve is still broken. That is how spigot-nms 1.2.20 shipped unusable.
//
// Unrecognised packages get the BASE floor rather than a pass. A table that skips what it does not
// recognise cannot fail, and adding a module would silently exempt it.

val BASE_FLOOR = 8

// Must agree with the floor table in versions/build.gradle.kts.
val moduleFloors = mapOf(
    "v1_8_R1" to 8, "v1_8_R2" to 8, "v1_8_R3" to 8, "v1_9_R1" to 8, "v1_9_R2" to 8,
    "v1_10_R1" to 8, "v1_11_R1" to 8, "v1_12_R1" to 8, "v1_13_R1" to 8, "v1_13_R2" to 8,
    "v1_14_R1" to 8, "v1_15_R1" to 8, "v1_16_R1" to 8, "v1_16_R2" to 8, "v1_16_R3" to 8,
    "worlds6" to 8,
    "v1_17_R1" to 16,
    "v1_18_R1" to 17, "v1_18_R2" to 17, "v1_19_R1" to 17, "v1_19_R2" to 17, "v1_19_R3" to 17,
    "v1_20_R1" to 17, "v1_20_R2" to 17, "v1_20_R3" to 17, "worlds7" to 17,
    "v1_20_CB" to 21, "v1_21_4" to 21, "v1_21_9" to 21, "v1_21_11" to 21, "v_latest" to 21,
)

fun majorFor(floor: Int) = floor + 44

// org.gradle.jvm.version on the outgoing shadow variant comes from the TOOLCHAIN, and neither
// options.release nor targetCompatibility feeds it. Only takes effect inside afterEvaluate, because
// the shadow plugin writes the attribute after us. Without this the jar is correct at major 52 while
// the metadata says 25 and no consumer below that can resolve it, which is exactly how
// spigot-nms 1.2.20 shipped unusable, and what check C below exists to catch.
afterEvaluate {
    configurations.named("shadowRuntimeElements").configure {
        attributes {
            attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, BASE_FLOOR)
        }
    }
}

/** (major, ownInternalName, referencedClassNames). Constant pool only, nothing is loaded. */
fun readClass(bytes: ByteArray): Triple<Int, String, Set<String>> {
    DataInputStream(bytes.inputStream()).use { input ->
        require(input.readInt() == -0x35014542) { "not a class file" }
        input.readUnsignedShort()
        val major = input.readUnsignedShort()
        val count = input.readUnsignedShort()
        val utf8 = HashMap<Int, String>()
        val classes = HashMap<Int, Int>()          // cp index -> name index
        var i = 1
        while (i < count) {
            when (val tag = input.readUnsignedByte()) {
                1 -> utf8[i] = input.readUTF()
                7 -> classes[i] = input.readUnsignedShort()
                8, 16, 19, 20 -> input.skipBytes(2)
                15 -> input.skipBytes(3)
                3, 4, 9, 10, 11, 12, 17, 18 -> input.skipBytes(4)
                5, 6 -> { input.skipBytes(8); i++ }
                else -> throw GradleException("unknown constant pool tag $tag")
            }
            i++
        }
        input.skipBytes(2)
        val thisIndex = input.readUnsignedShort()
        val own = utf8[classes[thisIndex]] ?: throw GradleException("could not read this_class")
        val refs = classes.values.mapNotNull { utf8[it] }
            .flatMap { if (it.startsWith("[")) Regex("L([^;]+);").findAll(it).map { m -> m.groupValues[1] }.toList() else listOf(it) }
            .toSet() - own
        return Triple(major, own, refs)
    }
}

val verifyFloors = tasks.register("verifyFloors") {
    group = "verification"
    description = "Checks the shaded jar against the per-module Java floor table."
    dependsOn(tasks.named("shadowJar"), tasks.named("generateMetadataFileForShadowPublication"))

    val jarFile = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }
    val classifier = tasks.named<Jar>("shadowJar").flatMap { it.archiveClassifier }
    val moduleFile = layout.buildDirectory.file("publications/shadow/module.json")
    val versionsDir = rootProject.file("versions")

    doLast {
        // The jar inspected below must be the one consumers receive. If shadowJar grows a
        // classifier the shaded jar publishes as -all and the primary artifact becomes the thin jar.
        if (classifier.get().isNotEmpty()) {
            throw GradleException(
                "shadowJar has classifier '${classifier.get()}', so the shaded jar is not the primary " +
                        "published artifact. Consumers would receive the thin jar."
            )
        }

        // class name -> floor, built from each module's own compiled output
        val floorOf = HashMap<String, Int>()
        var mapped = 0
        for ((module, floor) in moduleFloors) {
            val classes = File(versionsDir, "$module/build/classes/java/main")
            if (!classes.isDirectory) {
                throw GradleException(
                    "versions/$module has no compiled output at $classes, so its classes cannot be " +
                            "checked against its declared floor of Java $floor."
                )
            }
            classes.walkTopDown().filter { it.extension == "class" }.forEach {
                floorOf[it.relativeTo(classes).path.removeSuffix(".class").replace(File.separatorChar, '/')] = floor
                mapped++
            }
        }
        if (mapped < 250) {
            throw GradleException("only mapped $mapped version-module classes to floors; expected 250+, so the map is not being built")
        }

        var inspected = 0
        val ceilingViolations = ArrayList<String>()
        val downward = ArrayList<String>()

        ZipFile(jarFile.get().asFile).use { zip ->
            val entries = zip.entries().asSequence()
                .filter { it.name.endsWith(".class") }
                // Multi-release entries are governed by their own version directory; a JVM at the
                // base floor never reads them, so they are not a violation of it.
                .filterNot { it.name.startsWith("META-INF/versions/") }
                .toList()
            for (entry in entries) {
                val (major, own, refs) = readClass(zip.getInputStream(entry).readBytes())
                inspected++
                val floor = floorOf[own] ?: BASE_FLOOR
                if (major > majorFor(floor)) {
                    ceilingViolations.add("$own is major $major (Java ${major - 44}) but its floor is Java $floor")
                }
                for (ref in refs) {
                    val refFloor = floorOf[ref] ?: continue
                    if (refFloor > floor) {
                        downward.add("$own (Java $floor) references $ref (Java $refFloor)")
                    }
                }
            }
        }
        // A walker that matches nothing passes forever.
        if (inspected < 1000) {
            throw GradleException("verifyFloors only inspected $inspected classes, far below the expected count")
        }
        if (ceilingViolations.isNotEmpty()) {
            throw GradleException(
                "these classes exceed the floor declared for their module:\n  " +
                        ceilingViolations.take(20).joinToString("\n  ") +
                        "\nEither lower the bytecode or raise the module's floor, but do not ship them disagreeing."
            )
        }
        if (downward.isNotEmpty()) {
            throw GradleException(
                "a lower-floor class names a higher-floor one, so the JVM will resolve it during " +
                        "verification and the provider stops loading on every older server:\n  " +
                        downward.take(20).joinToString("\n  ") +
                        "\nRoute it through NmsBundles.forModule(...) instead."
            )
        }

        val module = moduleFile.get().asFile
        if (!module.exists()) { throw GradleException("expected Gradle module metadata at $module") }
        val declared = Regex("\"org\\.gradle\\.jvm\\.version\"\\s*:\\s*(\\d+)").find(module.readText())
            ?: throw GradleException("no org.gradle.jvm.version found in $module")
        if (declared.groupValues[1] != BASE_FLOOR.toString()) {
            throw GradleException(
                "published metadata declares org.gradle.jvm.version=${declared.groupValues[1]}, expected " +
                        "$BASE_FLOOR. The bytecode may be fine, but consumers below that cannot resolve this module."
            )
        }
        logger.lifecycle("verifyFloors: $inspected classes across ${moduleFloors.size} module floors, " +
                "no ceiling violations, no downward references, metadata declares $BASE_FLOOR")
    }
}
tasks.named("build") { dependsOn(verifyFloors) }
tasks.named("publish") { dependsOn(verifyFloors) }
