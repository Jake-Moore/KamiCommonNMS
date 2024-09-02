plugins {
    // Unique plugins for this module
}

dependencies {
    // Common Dependencies (compileOnly so they don't get picked up by shadowJar in :core)
    @Suppress("UNCHECKED_CAST")
    (rootProject.extra["commonDependencies"] as List<String>).forEach(dependencies::compileOnly)

    compileOnly(project.property("standaloneUtils") as String) // standalone-utils from KamiCommon
    compileOnly(project.property("lowestSpigotDep") as String)
}