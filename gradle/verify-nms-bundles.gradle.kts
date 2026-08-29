import java.io.DataInputStream
import java.util.zip.ZipFile

// Reflection buys the ability to ship modules targeting different JVMs, and gives up the compiler's
// guarantee that :core can actually reach them. Nothing would tell you at build time that a module
// lost its adapter, or that a dispatch table names a module that does not exist. You would find out
// on that Minecraft version, in production. These three assertions buy most of that back.
//
// They read bytecode rather than loading classes. That is deliberate and not a stylistic choice:
// once versions/* modules target their own Minecraft version's JVM, a build running on Java 21
// physically cannot load a module compiled for Java 25, so a test that instantiates adapters would
// have to be deleted the moment it became most useful.

/** Method names a class declares. Used to tell an implemented capability from an inherited default. */
fun declaredMethods(bytes: ByteArray): Set<String> {
    DataInputStream(bytes.inputStream()).use { input ->
        require(input.readInt() == -0x35014542) { "not a class file" }
        input.readUnsignedShort(); input.readUnsignedShort()
        val count = input.readUnsignedShort()
        val utf8 = HashMap<Int, String>()
        var i = 1
        while (i < count) {
            when (val tag = input.readUnsignedByte()) {
                1 -> utf8[i] = input.readUTF()
                7, 8, 16, 19, 20 -> input.skipBytes(2)
                15 -> input.skipBytes(3)
                3, 4, 9, 10, 11, 12, 17, 18 -> input.skipBytes(4)
                5, 6 -> { input.skipBytes(8); i++ }
                else -> throw GradleException("unknown constant pool tag $tag")
            }
            i++
        }
        input.skipBytes(6)                                      // access, this, super
        input.skipBytes(input.readUnsignedShort() * 2)           // interfaces
        fun skipAttrs() { repeat(input.readUnsignedShort()) { input.skipBytes(2); input.skipBytes(input.readInt()) } }
        repeat(input.readUnsignedShort()) { input.skipBytes(6); skipAttrs() }   // fields
        val methods = HashSet<String>()
        repeat(input.readUnsignedShort()) {
            input.skipBytes(2)
            methods.add(utf8[input.readUnsignedShort()] ?: "")
            input.skipBytes(2); skipAttrs()
        }
        return methods
    }
}

/** Class-file constant pool: every CONSTANT_Class name this class refers to. */
fun classRefs(bytes: ByteArray): Set<String> {
    DataInputStream(bytes.inputStream()).use { input ->
        require(input.readInt() == -0x35014542) { "not a class file" }
        input.readUnsignedShort(); input.readUnsignedShort()      // minor, major
        val count = input.readUnsignedShort()
        val utf8 = HashMap<Int, String>()
        val classes = ArrayList<Int>()
        var i = 1
        while (i < count) {
            when (val tag = input.readUnsignedByte()) {
                1 -> utf8[i] = input.readUTF()
                7 -> classes.add(input.readUnsignedShort()).let { }
                8, 16, 19, 20 -> input.skipBytes(2)
                15 -> input.skipBytes(3)
                3, 4, 9, 10, 11, 12, 17, 18 -> input.skipBytes(4)
                5, 6 -> { input.skipBytes(8); i++ }
                else -> throw GradleException("unknown constant pool tag $tag")
            }
            i++
        }
        // classes holds name_index values, resolved once the whole pool is read
        return classes.mapNotNull { utf8[it] }.toSet()
    }
}

val verifyNmsBundles = tasks.register("verifyNmsBundles") {
    group = "verification"
    description = "Fails if any versions/* module lost its NmsBundleImpl, or if one is named statically."
    dependsOn(tasks.named("shadowJar"))

    val jarFile = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }
    val settingsFile = rootProject.file("settings.gradle.kts")
    val coreSources = project.file("src/main/java")

    doLast {
        val declared = Regex("""include\("versions:([^"]+)"\)""")
            .findAll(settingsFile.readText()).map { it.groupValues[1] }.toList()

        // A regex that stopped matching would let every other assertion here pass vacuously.
        if (declared.size < 25) {
            throw GradleException(
                "only found ${declared.size} version modules in ${settingsFile.name}; expected at least 25, " +
                        "so this task is not reading the module list correctly."
            )
        }

        val bundleInterface = "com/kamikazejam/kamicommon/nms/bundle/NmsBundle"
        val implPattern = Regex("""^com/kamikazejam/kamicommon/nms/bundle/([^/]+)/NmsBundleImpl\.class$""")
        val missing = ArrayList<String>()
        val notImplementing = ArrayList<String>()
        val staticReferences = ArrayList<String>()
        var present = 0

        ZipFile(jarFile.get().asFile).use { zip ->
            val entries = zip.entries().asSequence().filter { it.name.endsWith(".class") }.toList()
            val byName = entries.associateBy { it.name }

            for (module in declared) {
                val entry = byName["com/kamikazejam/kamicommon/nms/bundle/$module/NmsBundleImpl.class"]
                if (entry == null) { missing.add(module); continue }
                present++
                val refs = classRefs(zip.getInputStream(entry).readBytes())
                if (bundleInterface !in refs) notImplementing.add(module)
            }

            // The invariant the whole refactor exists to hold: nothing names an adapter statically.
            // One `new NmsBundleImpl()` anywhere re-couples :core to a module's JVM target and takes
            // out every server older than that module.
            for (entry in entries) {
                if (implPattern.matches(entry.name)) continue
                val refs = classRefs(zip.getInputStream(entry).readBytes())
                val named = refs.filter { implPattern.matches("$it.class") }
                if (named.isNotEmpty()) staticReferences.add("${entry.name} -> ${named.joinToString()}")
            }
        }

        if (missing.isNotEmpty()) {
            throw GradleException(
                "these version modules have no NmsBundleImpl in the shaded jar: ${missing.joinToString()}. " +
                        ":core dispatches to them by name, so they would fail at runtime on exactly those " +
                        "Minecraft versions and nowhere else."
            )
        }
        if (notImplementing.isNotEmpty()) {
            throw GradleException(
                "these NmsBundleImpl classes do not reference $bundleInterface: ${notImplementing.joinToString()}"
            )
        }
        // Every capability a ladder asks a module for must actually be overridden by that module's
        // adapter. NmsBundle defaults each one to throwing, so a module that never implements a
        // capability still compiles, still ships, and still has an adapter, and then throws
        // UnsupportedOperationException on exactly the Minecraft versions that route to it.
        // This is not hypothetical: three EntityMethods rows in the ladder pointed at modules whose
        // adapters had no entityMethods() at all, and everything else here passed.
        val asked = HashMap<String, MutableSet<String>>()
        val direct = Regex("""forModule\("([^"]+)"\)\s*\.\s*(\w+)\s*\(""")
        val bundleOnly = Regex("""forModule\("([^"]+)"\)\s*;""")
        val viaHelper = Regex("""\b(?:componentBundle|metaBundle)\([^)]*\)\s*\.\s*(\w+)\s*\(""")
        coreSources.walkTopDown().filter { it.extension == "java" }.forEach { file ->
            val text = file.readText()
            direct.findAll(text).forEach { asked.getOrPut(it.groupValues[1]) { HashSet() }.add(it.groupValues[2]) }
            // Where a helper picks the module and the capability is called on its result, every
            // module the helper can return must implement every capability called on it.
            val helperModules = bundleOnly.findAll(text).map { it.groupValues[1] }.toList()
            if (helperModules.isNotEmpty()) {
                val caps = viaHelper.findAll(text).map { it.groupValues[1] }.toSet()
                helperModules.forEach { m -> asked.getOrPut(m) { HashSet() }.addAll(caps) }
            }
        }
        if (asked.size < 15) {
            throw GradleException(
                "only found capability calls for ${asked.size} modules in ${coreSources.name}; expected 15+, " +
                        "so this task is not reading the dispatch sites."
            )
        }
        val unimplemented = ArrayList<String>()
        ZipFile(jarFile.get().asFile).use { zip ->
            for ((module, caps) in asked) {
                val entry = zip.getEntry("com/kamikazejam/kamicommon/nms/bundle/$module/NmsBundleImpl.class")
                    ?: continue
                val have = declaredMethods(zip.getInputStream(entry).readBytes())
                caps.filterNot { it in have }.forEach { unimplemented.add("$module has no $it()") }
            }
        }
        if (unimplemented.isNotEmpty()) {
            throw GradleException(
                "a dispatch ladder asks a module for a capability its adapter does not implement, so it " +
                        "would fall through to NmsBundle's throwing default on exactly those Minecraft " +
                        "versions:\n  " + unimplemented.sorted().joinToString("\n  ")
            )
        }

        if (staticReferences.isNotEmpty()) {
            throw GradleException(
                "an NmsBundleImpl is named statically, which defeats the whole point of loading it by " +
                        "name. The verifier resolves it and every older server breaks:\n  " +
                        staticReferences.joinToString("\n  ")
            )
        }
        logger.lifecycle("verifyNmsBundles: $present/${declared.size} modules have an adapter, " +
                "${asked.values.sumOf { it.size }} capability calls all implemented, none named statically")
    }
}
tasks.named("build") { dependsOn(verifyNmsBundles) }
tasks.named("publish") { dependsOn(verifyNmsBundles) }
