@file:Suppress("PropertyName")

plugins {
    id("java")
    id("java-library")
    id("com.gradleup.shadow")
}

// Every class that NAMES the relocated Adventure lives here, and this module is shipped as a JAR
// INSIDE core's jar rather than shaded flat into it. Nested jar entries are not classpath entries,
// so javac cannot see them and no consumer can import Adventure, even a consumer who shades this
// library into their own uber jar. That is the property dependency scoping could not provide:
// measured 2026-08-30, a spigot-jar consumer compiled against the shaded Adventure successfully
// while it was declared runtime-only, because scope metadata cannot hide bytes that are present.
//
// At runtime ShimLoader loads this in a CHILD classloader. Delegation is parent-first, so these
// classes see VersionedComponent and TextBundle from the plugin classloader and implement them,
// while nothing above can see back in.
//
// No paperweight here, deliberately. None of these classes touch NMS types; they use Bukkit API that
// has existed since 1.8 plus the relocated Adventure. Compiling at release 8 makes every class
// major 52, so the same jar loads on a 1.8.8 server and a 26.2 one.

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
tasks.withType<JavaCompile> { options.release.set(8) }

// Resolves :text's relocated jar so shadowJar can merge it in.
//
// isTransitive = false is load-bearing. :text's dependency metadata still lists the raw net.kyori
// artifacts it relocated, so a transitive resolve pulls in UNrelocated Adventure alongside the
// relocated jar. That would put net.kyori.* into the nested jar and defeat the relocation entirely.
val shadedAdventure: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
}
dependencies { shadedAdventure(project(":text")) }

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":text"))
    // The HIGHEST Bukkit API any class here needs, not the lowest. These classes were written
    // against their own versions' APIs and CommandSender.spigot() does not exist in 1.11.2, so
    // pinning the oldest broke three of them. Compiling against a newer API is safe: they are
    // unchanged and call nothing they did not already call, and the runtime floor is set by
    // release 8, not by this pin.
    compileOnly("org.spigotmc:spigot-server:1.16.5-R0.1")
    compileOnly(project.property("jetbrainsDep") as String)
    compileOnly(project.property("standaloneUtils") as String)
}

tasks {
    build.get().dependsOn(shadowJar)
    shadowJar.get().dependsOn(jar)
    shadowJar {
        archiveClassifier.set("")
        // The relocated Adventure travels WITH these classes, in the same nested jar. They are the
        // only things that reference it, so they ship together or not at all.
        //
        // Unpacked explicitly rather than through shadow's `configurations` property, which merged
        // nothing here and produced a 22-entry jar with zero Adventure classes.
        from(shadedAdventure.elements.map { files -> files.map { zipTree(it.asFile) } })
    }
}

// Make the shadow jar this module's default outgoing artifact, exactly as :text does.
//
// Two things depend on it. Without this, project(":text-impl") resolves to the THIN jar, so :core
// would embed 15 classes instead of the 843 it needs. And Gradle cannot see the task dependency, so
// :core:shadowJar reads :text-impl's output without waiting for it to be built.
//
// Clearing outgoing.variants is the load-bearing half: without it Gradle can select the unshadowed
// `classes` variant, which for this purpose is the wrong content entirely.
configurations {
    named("apiElements") {
        outgoing.artifacts.clear(); outgoing.variants.clear()
        outgoing.artifact(tasks.named("shadowJar"))
    }
    named("runtimeElements") {
        outgoing.artifacts.clear(); outgoing.variants.clear()
        outgoing.artifact(tasks.named("shadowJar"))
    }
}
