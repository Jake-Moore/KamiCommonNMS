// Sets one module's Java floor: what its bytecode targets, and what it tells consumers.
//
// Applied as:
//     extra["moduleFloor"] = 8
//     extra["moduleToolchain"] = 21          // optional, defaults to 25
//     apply(from = "$rootDir/gradle/module-floor.gradle.kts")
//
// Four settings, each doing a different job, and none of them substitutes for another:
//
//   toolchain            javac must RUN on a JDK new enough to READ the classpath. paper-api 26.x
//                        is class-file major 69, so modules that see it need 25. Version modules
//                        keep 21: paperweight runs its workers in the module's toolchain JVM and
//                        that pairing is already proven, so it is left alone.
//   compileClasspath     the RESOLVER rejects a dependency declaring a higher jvm.version than the
//                        consumer asks for, before javac ever runs, so options.release cannot
//                        reach this.
//   options.release      what javac EMITS, and the only one of the four that decides whether a
//                        Java 8 server can load the class.
//   target/sourceCompat  what the outgoing variant DECLARES to consumers. Gradle derives
//                        org.gradle.jvm.version from this; options.release does not feed it.
//                        Get this wrong and the bytecode is fine while nobody can resolve it.
val floor = (project.extra["moduleFloor"] as Number).toInt()
val toolchainVersion = (project.extra.properties["moduleToolchain"] as? Number)?.toInt() ?: 25

extensions.configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(toolchainVersion))
    sourceCompatibility = JavaVersion.toVersion(floor)
    targetCompatibility = JavaVersion.toVersion(floor)
}
// paper-api 26.x declares org.gradle.jvm.version=25, and the resolver rejects it for a consumer
// asking for less, before javac runs, so options.release cannot reach this. Only the compile
// classpath needs it: nothing on the runtime side declares a floor above what these modules ask for.
configurations.named("compileClasspath").configure {
    attributes {
        attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, toolchainVersion)
    }
}

tasks.named<JavaCompile>("compileJava") { options.release.set(floor) }
