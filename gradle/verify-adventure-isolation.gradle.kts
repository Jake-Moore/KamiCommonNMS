import java.io.DataInputStream
import java.util.zip.ZipFile

// No NmsBundleImpl may name the shaded Adventure in a method signature.
//
// Why this shape, and what it does NOT claim. On 2026-08-30 a 26.2 server booted from a jar with the
// shaded Adventure deleted died at plugin enable:
//
//   NoClassDefFoundError: ...nms/text/kyori/adventure/text/Component
//       at NmsBundles.load(NmsBundles.java:54)
//       at CommandMapModifierProvider.provide(...)
//
// commandMapModifier dispatches to v1_17_R1, so loading that module's adapter for a capability with
// nothing to do with text dragged the shaded copy onto a server that has Adventure natively. The
// adapter declared componentFrom(Component), inherited from NmsBundle.
//
// I could not reproduce that load-time resolution outside a real server. Class.forName plus
// newInstance in an isolated URLClassLoader loads the same adapter cleanly, with the same bug
// deliberately reinjected and confirmed present in the jar. HotSpot defers this; Bukkit's
// PluginClassLoader evidently does not. So a behavioural probe would be a check whose failure branch
// I cannot demonstrate, which is worth nothing.
//
// This checks the PATTERN instead, which is deterministic: the shaded type must not appear in any
// adapter's method signatures. That is exactly the shape that broke, it is provable, and its failure
// branch is demonstrated by reinjecting the bug. It does not claim to catch every possible way a
// module could reach the shaded copy; the server-level strip test in
// runs/2026-08-30-adventure-strip-acceptance.md remains the behavioural acceptance.

val SHADED_DESCRIPTOR = "Lcom/kamikazejam/kamicommon/nms/text/kyori/"

/** Every method descriptor a class declares, read straight from the constant pool. */
fun methodDescriptors(bytes: ByteArray): List<Pair<String, String>> {
    DataInputStream(bytes.inputStream()).use { input ->
        require(input.readInt() == -0x35014542) { "not a class file" }
        input.readUnsignedShort(); input.readUnsignedShort()
        val count = input.readUnsignedShort()
        val utf8 = HashMap<Int, String>()
        var i = 1
        while (i < count) {
            when (input.readUnsignedByte()) {
                1 -> utf8[i] = input.readUTF()
                7, 8, 16, 19, 20 -> input.skipBytes(2)
                15 -> input.skipBytes(3)
                3, 4, 9, 10, 11, 12, 17, 18 -> input.skipBytes(4)
                5, 6 -> { input.skipBytes(8); i++ }
                else -> throw IllegalStateException("unknown constant pool tag")
            }
            i++
        }
        input.skipBytes(2); input.skipBytes(2); input.skipBytes(2)
        input.skipBytes(input.readUnsignedShort() * 2)                       // interfaces
        repeat(input.readUnsignedShort()) {                                  // fields
            input.skipBytes(6)
            repeat(input.readUnsignedShort()) { input.skipBytes(2); input.skipBytes(input.readInt()) }
        }
        val out = ArrayList<Pair<String, String>>()
        repeat(input.readUnsignedShort()) {                                  // methods
            input.skipBytes(2)
            val name = utf8[input.readUnsignedShort()] ?: "?"
            val desc = utf8[input.readUnsignedShort()] ?: "?"
            out.add(name to desc)
            repeat(input.readUnsignedShort()) { input.skipBytes(2); input.skipBytes(input.readInt()) }
        }
        return out
    }
}

val verifyAdventureIsolation = tasks.register("verifyAdventureIsolation") {
    group = "verification"
    description = "No NMS adapter may name the shaded Adventure in a method signature"

    val shadowJarTask = tasks.named<org.gradle.jvm.tasks.Jar>("shadowJar")
    dependsOn(shadowJarTask)
    val jarFile = shadowJarTask.flatMap { it.archiveFile }
    inputs.file(jarFile)
    outputs.upToDateWhen { false }

    doLast {
        val offenders = ArrayList<String>()
        var adapters = 0
        var bridges = 0
        ZipFile(jarFile.get().asFile).use { zip ->
            for (entry in zip.entries()) {
                val n = entry.name
                if (!n.endsWith("/NmsBundleImpl.class") && !n.endsWith("/ShadedComponentBridgeImpl.class")) continue
                val isBridge = n.endsWith("/ShadedComponentBridgeImpl.class")
                if (isBridge) bridges++ else adapters++
                if (isBridge) continue   // the bridge exists to name it; that is its whole job
                val bytes = zip.getInputStream(entry).readBytes()
                for ((name, desc) in methodDescriptors(bytes)) {
                    if (desc.contains(SHADED_DESCRIPTOR)) {
                        offenders.add("$n  ->  $name$desc")
                    }
                }
            }
        }

        // A scan that matched almost nothing reports success no matter what is wrong.
        if (adapters < 25) {
            throw GradleException(
                "only $adapters NmsBundleImpl classes were found in the jar. This project has 31 " +
                        "version modules, so a scan finding almost none is looking in the wrong place " +
                        "and proves nothing."
            )
        }
        // The bridges are the positive control: they are the one place the shaded type is allowed, so
        // if none exist the exemption above is silently covering nothing.
        if (bridges == 0) {
            throw GradleException(
                "no ShadedComponentBridgeImpl found. Either they were removed, in which case the " +
                        "shaded-Adventure entry point moved somewhere this check is not looking, or the " +
                        "jar is not what it should be."
            )
        }

        if (offenders.isNotEmpty()) {
            throw GradleException(
                "these adapters name the shaded Adventure in a method signature:\n  " +
                        offenders.joinToString("\n  ") +
                        "\n\nAn adapter is loaded whenever ANY capability from its module is used. On " +
                        "26.2 that happens through commandMapModifier reaching v1_17_R1, and it dragged " +
                        "the shaded copy onto a server that has Adventure natively and never touches it." +
                        "\nMove the method to ShadedComponentBridge, which is loaded on demand."
            )
        }
        println("verifyAdventureIsolation: $adapters adapters name no shaded Adventure in any " +
                "signature, $bridges bridges hold it on purpose")
    }
}

tasks.named("check") { dependsOn(verifyAdventureIsolation) }
