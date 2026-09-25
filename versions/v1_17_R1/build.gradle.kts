import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

plugins {
    // Unique plugins for this module
    id("io.papermc.paperweight.userdev") // 1. add the Paperweight plugin
}

dependencies {
    // Unique dependencies for this module
    paperweight.paperDevBundle("1.17.1-R0.1-SNAPSHOT") // 2. add the dev bundle (contains all apis)
    compileOnly(project(":versions:v1_13_R1"))
    compileOnly(project(":versions:v1_14_R1"))
}

// The 1.17.1 bundle's source patches were written against a decompile made on Java 17, and paperweight
// now decompiles on 21. Since JDK 19 (JDK-4511638) Float.toString spells one WorldBorder constant
// differently, so three context lines of WorldBorder.java.patch no longer match and setup fails. This
// transform respells them for Java 21. It changes context only: both spellings are the same value, and
// nothing Paper adds is touched.
val worldBorderRespelled: Attribute<Boolean> = Attribute.of("kamicommon.worldBorderRespelled", Boolean::class.javaObjectType)

abstract class RespellWorldBorderPatch : TransformAction<TransformParameters.None> {
    @get:InputArtifact
    abstract val input: Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val source = input.get().asFile
        val target = outputs.file("${source.nameWithoutExtension}-worldborder.zip")
        val patch = "patches/net/minecraft/world/level/border/WorldBorder.java.patch"
        val java17 = "5.9999968E7D"
        var found = false
        ZipInputStream(source.inputStream().buffered()).use { zin ->
            ZipOutputStream(target.outputStream().buffered()).use { zout ->
                generateSequence { zin.nextEntry }.forEach { entry ->
                    zout.putNextEntry(ZipEntry(entry.name))
                    if (entry.name == patch) {
                        found = true
                        val text = zin.readBytes().toString(Charsets.UTF_8)
                        val count = text.windowed(java17.length).count { it == java17 }
                        check(count == 3) { "$patch has $count occurrences of $java17, expected 3. Re-derive this transform." }
                        zout.write(text.replace(java17, "(double)5.999997E7F").toByteArray(Charsets.UTF_8))
                    } else {
                        zin.copyTo(zout)
                    }
                    zout.closeEntry()
                }
            }
        }
        check(found) { "$patch is not in ${source.name}" }
    }
}

dependencies {
    attributesSchema { attribute(worldBorderRespelled) }
    artifactTypes.maybeCreate("zip").attributes.attribute(worldBorderRespelled, false)
    registerTransform(RespellWorldBorderPatch::class) {
        from.attribute(worldBorderRespelled, false).attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, "zip")
        to.attribute(worldBorderRespelled, true).attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, "zip")
    }
}
configurations.named("paperweightDevelopmentBundle") {
    attributes.attribute(worldBorderRespelled, true)
}

tasks { // 3. configure tasks (like reObf automatically)
    assemble {
        dependsOn(reobfJar)
    }}
