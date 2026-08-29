plugins {
    // Unique plugins for this module
    id("java")
    id("java-library")
}

allprojects {
    dependencies {
        // Lombok
        compileOnly(project.property("lombokDep") as String)
        annotationProcessor(project.property("lombokDep") as String)
        testImplementation(project.property("lombokDep") as String)
        testAnnotationProcessor(project.property("lombokDep") as String)

        // IntelliJ annotations
        compileOnly(project.property("jetbrainsDep") as String)
    }
}

dependencies {
    // Common Dependencies (compileOnly so they don't get picked up by shadowJar in :core)
    @Suppress("UNCHECKED_CAST")
    (rootProject.extra["commonDependencies"] as List<String>).forEach(dependencies::compileOnly)

    compileOnly(project.property("standaloneUtils") as String) // standalone-utils from KamiCommon
    compileOnly(project.property("serverAPI") as String)

    // Add nms-text for text components
    compileOnly(project.property("adventureDep") as String)
}

// Under MC_SERVER_NEWEST_API=true, `serverAPI` is paper-api 26.2, whose Gradle
// metadata declares org.gradle.jvm.version=25. Ask the resolver for a 25-compatible
// library, run javac on JDK 25 so it can read class-file major 69, but keep EMITTING
// Java 21 bytecode - the providers here must load on pre-26 JVMs, and the JVM loads
// referenced classes during verification rather than lazily.
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    // Declare the Java 21 target explicitly. options.release below controls what javac EMITS,
    //  but it does not feed org.gradle.jvm.version on the outgoing variants - those default to
    //  the toolchain, which would publish metadata claiming Java 25 over major-65 bytecode and
    //  make every Java 21 consumer unable to resolve this module.
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
configurations.named("compileClasspath").configure {
    attributes {
        attribute(org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}
tasks.withType<JavaCompile>().configureEach { options.release.set(21) }