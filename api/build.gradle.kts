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
    compileOnly(project.property("lowestSpigotDep") as String)
}

// Provision Java 17 (no J21 requirements here yet)
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}