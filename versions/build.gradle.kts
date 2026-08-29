// Give all versions access to api and standalone-utils
subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    // Each module targets the JVM its own Minecraft version required, rather than one number for
    // all of them. That is only safe because :core resolves these modules by name. See
    // com.kamikazejam.kamicommon.nms.bundle.NmsBundles. If anything ever names one statically again,
    // the verifier resolves it during :core's own verification and every older server stops loading
    // the provider that did it. verifyNmsBundles is what stops that.
    //
    // The TOOLCHAIN stays 21 for every module except v_latest, and is deliberately NOT the floor.
    // paperweight runs its workers in the module's toolchain JVM, so a module compiling to Java 17
    // still runs javac and paperweight on 21. That is what lets paperweight-userdev sit at
    // 2.0.0-beta.23, whose own task classes are Java 21: no module hosts them on anything lower.
    // Only the emitted target moves with the floor.

    // The floor table lives in gradle/module-floors.properties so that this file, verifyFloors and
    // verifyDispatchFloors all read one copy. See that file for why each module sits where it does.
    val floors = java.util.Properties().apply {
        rootProject.file("gradle/module-floors.properties").inputStream().use { load(it) }
    }.entries.associate { (k, v) -> k.toString() to v.toString().trim().toInt() }
    if (floors.size < 30) {
        throw GradleException(
            "gradle/module-floors.properties yielded only ${floors.size} entries. It should carry " +
                    "every module under versions/, and a table that parsed to almost nothing would " +
                    "hand most modules the no-entry error below rather than their real floor."
        )
    }
    val floor = floors[project.name]
        ?: throw GradleException(
            "versions/${project.name} has no entry in gradle/module-floors.properties. " +
                    "Add one: a module with no declared floor would silently inherit whatever the " +
                    "build happened to be running, which is how every module ended up at Java 21."
        )
    // v_latest compiles against paper-api 26.x, class-file major 69, which javac must run on 25 to read.
    val toolchainVersion = if (project.name == "v_latest") 25 else 21

    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(toolchainVersion))
            sourceCompatibility = JavaVersion.toVersion(floor)
            targetCompatibility = JavaVersion.toVersion(floor)
        }
        tasks.withType<JavaCompile>().configureEach { options.release.set(floor) }
        // paper-api 26.x declares org.gradle.jvm.version=25, and the resolver rejects it for a
        // consumer asking for less, before javac runs, so options.release cannot reach it. Only
        // v_latest sees that dependency, but setting it for every module keeps them consistent.
        configurations.named("compileClasspath").configure {
            attributes {
                attribute(
                    org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                    toolchainVersion
                )
            }
        }

        // After removing the "java" plugin from this parent module (:versions), calls to "compileOnly" in subprojects broke.
        //  Fortunately, we can still add dependencies manually using this method.
        dependencies.add("compileOnly", project(":api"))
        // standalone-utils from KamiCommon
        dependencies.add("compileOnly", project.property("standaloneUtils") as String)
        // Common Dependencies
        @Suppress("UNCHECKED_CAST")
        (rootProject.extra["commonDependencies"] as List<String>).forEach { dep ->
            dependencies.add("compileOnly", dep)
        }
        // Lombok
        dependencies.add("compileOnly", project.property("lombokDep") as String)
        dependencies.add("annotationProcessor", project.property("lombokDep") as String)
        dependencies.add("testImplementation", project.property("lombokDep") as String)
        dependencies.add("testAnnotationProcessor", project.property("lombokDep") as String)
        // IntelliJ annotations
        dependencies.add("compileOnly", project.property("jetbrainsDep") as String)

        // Add our shaded Adventure version so that subprojects can use it in their code
        dependencies.add("compileOnly", project.dependencies.project(project.property("adventureDep") as String))
    }
}