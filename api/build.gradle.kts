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
    compileOnly(project(project.property("adventureDep") as String))
}

// A 1.8.8 server loads every class in this module, so it is the floor everything else
// is measured against. See gradle/module-floor.gradle.kts for what each setting does.
extra["moduleFloor"] = 8
apply(from = "$rootDir/gradle/module-floor.gradle.kts")
