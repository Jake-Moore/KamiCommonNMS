import java.io.ByteArrayOutputStream
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

// The relocated Adventure must exist ONLY inside internal-libs/adventure.jar, and nothing outside it
// may reference the relocated package at all.
//
// Why nested rather than shaded flat. The relocated copy serves servers with no native Adventure,
// meaning everything below 1.18.2. Shaded flat, its classes are ordinary classpath entries and every
// consumer can import them. Dependency scoping cannot fix that: measured 2026-08-30, a spigot-jar
// consumer compiled against the shaded Adventure successfully while it was declared runtime-only,
// because scope metadata cannot hide bytes that are physically present. Nested, the classes are not
// classpath entries at all, and that survives a consumer shading this library into their own uber
// jar, which is the property scoping could not give.
//
// This check is deliberately stricter than the one it replaces. That one allowed the relocated
// package in any class except an NmsBundleImpl signature. Now the answer is simply zero, outside the
// nested jar, which is an invariant with no exemption list to rot.

val RELOCATED = "com/kamikazejam/kamicommon/nms/text/kyori/"
val RELOCATED_DESC = "Lcom/kamikazejam/kamicommon/nms/text/kyori/"

val verifyAdventureIsolation = tasks.register("verifyAdventureIsolation") {
    group = "verification"
    description = "The relocated Adventure exists only inside the nested jar"

    val shadowJarTask = tasks.named<org.gradle.jvm.tasks.Jar>("shadowJar")
    dependsOn(shadowJarTask)
    val jarFile = shadowJarTask.flatMap { it.archiveFile }
    inputs.file(jarFile)
    outputs.upToDateWhen { false }

    doLast {
        val jar = jarFile.get().asFile
        var nested: ByteArray? = null
        var looseRelocated = 0
        val offenders = ArrayList<String>()
        var scanned = 0

        ZipFile(jar).use { zip ->
            for (entry in zip.entries()) {
                val n = entry.name
                if (n == "internal-libs/adventure.jar") {
                    nested = zip.getInputStream(entry).readBytes()
                    continue
                }
                if (n.startsWith(RELOCATED)) { looseRelocated++; continue }
                if (!n.endsWith(".class")) continue
                scanned++
                // Any mention at all, in a signature or a body. There is no legitimate reference to
                // the relocated package from outside the nested jar any more.
                val text = String(zip.getInputStream(entry).readBytes(), Charsets.ISO_8859_1)
                if (text.contains(RELOCATED) || text.contains(RELOCATED_DESC)) {
                    offenders.add(n)
                }
            }
        }

        // 1. the nested jar must be there, or nothing renders text below 1.18.2
        val bytes = nested ?: throw GradleException(
            "internal-libs/adventure.jar is missing from the shipped jar. It carries the relocated " +
                    "Adventure that every server below 1.18.2 needs. A build that drops it produces a " +
                    "library that cannot render text on those versions."
        )

        // 2. it must actually contain Adventure, not be an empty shell
        var nestedEntries = 0
        var nestedRelocated = 0
        ZipInputStream(bytes.inputStream()).use { zin ->
            while (true) {
                val e = zin.nextEntry ?: break
                nestedEntries++
                if (e.name.startsWith(RELOCATED)) nestedRelocated++
            }
        }
        // The nested jar must also carry the IMPLEMENTATIONS, not just the library. Embedding :text
        // instead of :text-impl produced a jar with 839 Adventure classes and no implementations,
        // which every other check passed and which would have thrown ClassNotFoundException on the
        // first text call on any server below 1.18.2.
        var nestedImpls = 0
        ZipInputStream(bytes.inputStream()).use { zin ->
            while (true) {
                val e = zin.nextEntry ?: break
                if (e.name.endsWith(".class") && e.name.contains("TextBundleImpl_")) nestedImpls++
            }
        }
        if (nestedImpls < 4) {
            throw GradleException(
                "internal-libs/adventure.jar contains only $nestedImpls TextBundleImpl classes; there " +
                        "is one per shaded tier and there should be at least 4. The nested jar was " +
                        "built from the relocated Adventure alone rather than from :text-impl, so " +
                        "TextBundles.forModule would throw ClassNotFoundException at runtime on every " +
                        "server below 1.18.2."
            )
        }
        if (nestedRelocated < 500) {
            throw GradleException(
                "internal-libs/adventure.jar holds only $nestedRelocated relocated Adventure classes " +
                        "out of $nestedEntries entries; it should hold over 800. Either the relocation " +
                        "target moved and this check is looking for a package that no longer exists, or " +
                        "the nested jar was built from the wrong thing."
            )
        }

        // 3. the nested jar's own classes must still meet the floor. verifyFloors walks loose entries
        //    and cannot see inside a jar-in-jar, so without this the classes that moved in there
        //    stopped being floor-checked entirely. They load on 1.8.8, so they must be major 52.
        var worstMajor = 0
        var worstName = ""
        ZipInputStream(bytes.inputStream()).use { zin ->
            while (true) {
                val e = zin.nextEntry ?: break
                if (!e.name.endsWith(".class")) continue
                val head = ByteArrayOutputStream()
                val buf = ByteArray(8)
                var read = 0
                while (read < 8) {
                    val n = zin.read(buf, 0, 8 - read)
                    if (n <= 0) break
                    head.write(buf, 0, n); read += n
                }
                val b = head.toByteArray()
                if (b.size < 8) continue
                val major = ((b[6].toInt() and 0xFF) shl 8) or (b[7].toInt() and 0xFF)
                if (major > worstMajor) { worstMajor = major; worstName = e.name }
            }
        }
        if (worstMajor > 52) {
            throw GradleException(
                "the nested jar contains class-file major $worstMajor ($worstName), above Java 8. " +
                        "Everything in there loads on a 1.8.8 server through the child classloader, so " +
                        "anything above major 52 is an UnsupportedClassVersionError waiting for the " +
                        "first old server that touches text."
            )
        }

        // 4. nothing loose, anywhere
        if (looseRelocated > 0) {
            throw GradleException(
                "$looseRelocated relocated Adventure classes are loose in the jar. They must live " +
                        "ONLY inside internal-libs/adventure.jar. Loose entries are classpath entries, " +
                        "and every consumer can import them."
            )
        }
        if (offenders.isNotEmpty()) {
            throw GradleException(
                "these classes reference the relocated Adventure from outside the nested jar:\n  " +
                        offenders.sorted().joinToString("\n  ") +
                        "\n\nThey will fail at runtime, because the relocated package is loaded by a " +
                        "CHILD classloader they cannot see into. Move them into :text-impl, or route " +
                        "through TextBundle, which names nothing relocated."
            )
        }

        // A scan that inspected almost nothing passes forever.
        if (scanned < 300) {
            throw GradleException(
                "only $scanned classes were scanned, far below the expected count. This check is " +
                        "looking at the wrong artifact and proves nothing."
            )
        }
        println("verifyAdventureIsolation: $scanned classes outside the nested jar reference no " +
                "relocated Adventure, $nestedRelocated relocated classes and $nestedImpls text " +
                "implementations sealed inside it, highest major $worstMajor")
    }
}

tasks.named("check") { dependsOn(verifyAdventureIsolation) }
