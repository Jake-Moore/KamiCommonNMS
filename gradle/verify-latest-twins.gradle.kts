import java.io.DataInputStream
import java.util.zip.ZipFile

// The fifth check, and the one that makes the v_latest canary mean something.
//
// versions/v_latest exists so that bumping highestPaperDep recompiles the implementations a 26.x
// server runs. That only warns about what it MIRRORS. Nothing held a _LATEST class to its twin, so
// a method added to VersionedComponent_1_21_4 and not to VersionedComponent_LATEST left the canary
// silently checking a smaller surface than the one that ships, and every other check passed.
//
// It reads the shaded jar rather than the source, for two reasons. The jar is what ships, and the
// twins target different JVMs, so a build running on Java 21 physically cannot load the Java 25
// copy. This is the same reasoning verify-nms-bundles.gradle.kts records.
//
// WHAT "SAME SURFACE" MEANS HERE. The twins are not byte-identical and are not required to be. They
// carry different class names, different javadoc, and each names its own type in `instanceof` and
// in the constructors it calls. What is compared is only what a caller outside the class can
// observe:
//
//   1. every public and protected method and constructor, as visibility + static + abstract + name
//      + JVM descriptor, so that a changed parameter or return type is a difference;
//   2. the superclass and the directly implemented interfaces.
//
// Each twin's own internal name is normalised to SELF on both sides before comparing, because a
// factory returning its own type is the same declaration in both copies.
//
// Excluded, deliberately: private and package-private members, fields, method bodies, javadoc, and
// synthetic members including the bridges javac derives from the two lists above. None of those is
// reachable from a dispatch site, and the twins are meant to differ in some of them.
//
// (2) is not decoration. VersionedComponent_LATEST implemented VersionedComponent while its twin
// implemented ModernVersionedComponent, so the two answered `instanceof ModernVersionedComponent`
// differently while declaring identical methods.

/** Superclass, interfaces, and the public surface of one class file. */
fun surfaceOf(bytes: ByteArray): Triple<String, List<String>, Set<String>> {
    DataInputStream(bytes.inputStream()).use { input ->
        require(input.readInt() == -0x35014542) { "not a class file" }
        input.readUnsignedShort(); input.readUnsignedShort()      // minor, major
        val count = input.readUnsignedShort()
        val utf8 = HashMap<Int, String>()
        val classNameIndex = HashMap<Int, Int>()
        var i = 1
        while (i < count) {
            when (val tag = input.readUnsignedByte()) {
                1 -> utf8[i] = input.readUTF()
                7 -> classNameIndex[i] = input.readUnsignedShort()
                8, 16, 19, 20 -> input.skipBytes(2)
                15 -> input.skipBytes(3)
                3, 4, 9, 10, 11, 12, 17, 18 -> input.skipBytes(4)
                5, 6 -> { input.skipBytes(8); i++ }
                else -> throw GradleException("unknown constant pool tag $tag")
            }
            i++
        }
        fun className(index: Int): String =
            if (index == 0) "" else utf8[classNameIndex[index]] ?: "?"

        input.skipBytes(2)                                        // access flags
        input.skipBytes(2)                                        // this_class
        val superName = className(input.readUnsignedShort())
        val interfaces = (1..input.readUnsignedShort()).map { className(input.readUnsignedShort()) }

        fun skipAttrs() {
            repeat(input.readUnsignedShort()) { input.skipBytes(2); input.skipBytes(input.readInt()) }
        }
        repeat(input.readUnsignedShort()) { input.skipBytes(6); skipAttrs() }   // fields

        val methods = LinkedHashSet<String>()
        repeat(input.readUnsignedShort()) {
            val access = input.readUnsignedShort()
            val name = utf8[input.readUnsignedShort()] ?: "?"
            val descriptor = utf8[input.readUnsignedShort()] ?: "?"
            skipAttrs()
            val synthetic = access and 0x1000 != 0 || access and 0x0040 != 0
            val visible = access and 0x0001 != 0 || access and 0x0004 != 0
            if (!synthetic && visible) {
                val words = ArrayList<String>()
                words.add(if (access and 0x0001 != 0) "public" else "protected")
                if (access and 0x0008 != 0) words.add("static")
                if (access and 0x0400 != 0) words.add("abstract")
                methods.add("${words.joinToString(" ")} $name$descriptor")
            }
        }
        return Triple(superName, interfaces.sorted(), methods)
    }
}

// Every canary states its own twin in its class javadoc, so the pairing is DECLARED, not guessed
// from the name. Guessing would have to know that BlockUtil_LATEST pairs with BlockUtil1_21_11 and
// Teleporter_LATEST with Teleporter1_21_9, and would silently pair the wrong two the day a module
// is added.
//
// A _LATEST class with no such sentence is not a canary: it is the implementation 26.x actually
// dispatches to, and its counterpart in a lower module is a fork that is MEANT to differ. Those are
// the six capabilities whose ladders already terminate at v_latest.
val twinDeclaration = Regex("""twin of \{@code (\w+)} in \{@code versions/(\w+)}""")

// The count this project has today. A floor rather than an equality, because adding a canary must
// not fail the build, and a hardcoded number with no floor is how a check that stopped matching
// keeps reporting success. Deleting the javadoc sentence to silence a failing comparison is the
// obvious wrong move, and lowering this line is what that move now costs.
val MINIMUM_DECLARED_TWINS = 9

val verifyLatestTwins = tasks.register("verifyLatestTwins") {
    group = "verification"
    description = "Fails if a _LATEST canary and the twin it names no longer declare the same public surface."
    dependsOn(tasks.named("shadowJar"))

    val jarFile = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }
    val latestDir = rootProject.file("versions/v_latest/src/main/java")
    val versionsDir = rootProject.file("versions")

    doLast {
        // name of the canary -> (name of the twin, module the twin is declared to live in)
        val declared = LinkedHashMap<String, Pair<String, String>>()
        latestDir.walkTopDown().filter { it.name.endsWith("_LATEST.java") }.sortedBy { it.name }
            .forEach { file ->
                val match = twinDeclaration.find(file.readText()) ?: return@forEach
                declared[file.name.removeSuffix(".java")] =
                    match.groupValues[1] to match.groupValues[2]
            }

        if (declared.size < MINIMUM_DECLARED_TWINS) {
            throw GradleException(
                "only ${declared.size} classes in versions/v_latest declare the twin they mirror, and " +
                        "this project has at least $MINIMUM_DECLARED_TWINS. Either the javadoc sentence " +
                        "this check reads has been reworded, or a canary lost it, and in both cases the " +
                        "comparison below is running on a smaller set than it should."
            )
        }

        // The declared module must really hold the declared twin. A rename that moved one and left
        // the sentence behind would otherwise pair against a class that no longer exists, and the
        // jar lookup would report it as missing without saying the declaration was the stale part.
        val misdeclared = declared.entries.filter { (_, twin) ->
            versionsDir.resolve(twin.second).resolve("src/main/java").walkTopDown()
                .none { it.name == "${twin.first}.java" }
        }.map { "${it.key} names ${it.value.first} in versions/${it.value.second}, which has no such file" }
        if (misdeclared.isNotEmpty()) {
            throw GradleException(
                "a canary names a twin that is not where it says it is:\n  " +
                        misdeclared.joinToString("\n  ") +
                        "\nCorrect the javadoc sentence in versions/v_latest, or move the twin back."
            )
        }

        val differences = ArrayList<String>()
        var compared = 0

        ZipFile(jarFile.get().asFile).use { zip ->
            val bySimpleName = zip.entries().asSequence()
                .filter { it.name.endsWith(".class") && !it.name.contains('$') }
                .groupBy { it.name.removeSuffix(".class").substringAfterLast('/') }

            for ((canaryName, twin) in declared) {
                val (twinName, _) = twin
                val canaryEntries = bySimpleName[canaryName].orEmpty()
                val twinEntries = bySimpleName[twinName].orEmpty()
                // Ambiguity is a failure, not a pick. Two classes with one simple name would make
                // the comparison depend on zip ordering.
                if (canaryEntries.size != 1 || twinEntries.size != 1) {
                    throw GradleException(
                        "expected exactly one $canaryName and one $twinName in the shaded jar, found " +
                                "${canaryEntries.size} and ${twinEntries.size}. Neither the canary nor " +
                                "its twin can be compared until that is unambiguous."
                    )
                }
                val canaryEntry = canaryEntries.single()
                val twinEntry = twinEntries.single()
                val canaryInternal = canaryEntry.name.removeSuffix(".class")
                val twinInternal = twinEntry.name.removeSuffix(".class")

                // Each twin names its own type in factory returns and in instanceof. That is the
                // same declaration on both sides, so both internal names collapse to SELF before
                // anything is compared.
                fun normalise(text: String): String =
                    text.replace(canaryInternal, "SELF").replace(twinInternal, "SELF")

                val canary = surfaceOf(zip.getInputStream(canaryEntry).readBytes())
                val twinSurface = surfaceOf(zip.getInputStream(twinEntry).readBytes())
                compared++

                val canarySuper = normalise(canary.first)
                val twinSuper = normalise(twinSurface.first)
                if (canarySuper != twinSuper) {
                    differences.add("$canaryName extends $canarySuper, $twinName extends $twinSuper")
                }

                val canaryInterfaces = canary.second.map(::normalise).toSet()
                val twinInterfaces = twinSurface.second.map(::normalise).toSet()
                (canaryInterfaces - twinInterfaces).sorted().forEach {
                    differences.add("$canaryName implements $it, $twinName does not")
                }
                (twinInterfaces - canaryInterfaces).sorted().forEach {
                    differences.add("$twinName implements $it, $canaryName does not")
                }

                val canaryMethods = canary.third.map(::normalise).toSet()
                val twinMethods = twinSurface.third.map(::normalise).toSet()
                (canaryMethods - twinMethods).sorted().forEach {
                    differences.add("$canaryName declares '$it', $twinName does not")
                }
                (twinMethods - canaryMethods).sorted().forEach {
                    differences.add("$twinName declares '$it', $canaryName does not")
                }
            }
        }

        // A comparison that read no methods at all would pass on any pair. The smallest twin in this
        // project still declares a constructor and one method.
        if (compared != declared.size) {
            throw GradleException(
                "declared ${declared.size} twin pairs but compared $compared, so some pair was skipped."
            )
        }

        if (differences.isNotEmpty()) {
            throw GradleException(
                "a versions/v_latest canary and the twin it mirrors no longer declare the same public " +
                        "surface. The canary only proves that what it MIRRORS still compiles against " +
                        "Paper ${rootProject.property("highestPaperDep")}, so anything the twin has and " +
                        "the canary does not is unchecked, and anything the canary has and the twin does " +
                        "not is untested on the versions that dispatch to the twin:\n  " +
                        differences.sorted().joinToString("\n  ") +
                        "\nMake the change in both, or state why they must differ and change what this " +
                        "check compares."
            )
        }

        logger.lifecycle(
            "verifyLatestTwins: $compared canary/twin pairs declare the same public surface"
        )
    }
}
tasks.named("build") { dependsOn(verifyLatestTwins) }
tasks.named("publish") { dependsOn(verifyLatestTwins) }
