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
    // The TOOLCHAIN stays 21 for every module except v_latest. paperweight runs its workers in the
    // module's toolchain JVM and that pairing is already proven at 2.0.0-beta.22; the floor work has
    // no reason to disturb it. Only the emitted target moves.
    val floors = mapOf(
        // Minecraft 1.8 - 1.16.5 ran on Java 8, and WorldEdit/WorldGuard 6 are Java 7 bytecode.
        "v1_8_R1" to 8, "v1_8_R2" to 8, "v1_8_R3" to 8,
        "v1_9_R1" to 8, "v1_9_R2" to 8, "v1_10_R1" to 8, "v1_11_R1" to 8, "v1_12_R1" to 8,
        "v1_13_R1" to 8, "v1_13_R2" to 8, "v1_14_R1" to 8, "v1_15_R1" to 8,
        "v1_16_R1" to 8, "v1_16_R2" to 8, "v1_16_R3" to 8,
        "worlds6" to 8,
        // 1.17 is the one release that required exactly 16.
        "v1_17_R1" to 16,
        // 1.18 - 1.20.4 required 17. worlds7 joins them because worldguard-bukkit 7.0.9 is
        // entirely class-file major 61, so a lower target here would be a promise we cannot keep.
        "v1_18_R1" to 17, "v1_18_R2" to 17,
        "v1_19_R1" to 17, "v1_19_R2" to 17, "v1_19_R3" to 17,
        "v1_20_R1" to 17, "v1_20_R2" to 17, "v1_20_R3" to 17,
        "worlds7" to 17,
        // 1.20.5 onward required 21. v_latest is compiled against Paper 26.x but still emits 21,
        // because nothing needs it higher and a major-69 class in the jar would only be protected
        // by the reflection never being bypassed.
        "v1_20_CB" to 21, "v1_21_4" to 21, "v1_21_9" to 21, "v1_21_11" to 21,
        "v_latest" to 21,
    )
    val floor = floors[project.name]
        ?: throw GradleException(
            "versions/${project.name} has no entry in the floor table in versions/build.gradle.kts. " +
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