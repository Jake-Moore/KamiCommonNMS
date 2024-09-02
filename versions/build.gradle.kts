// Give all versions access to api and standalone-utils
subprojects {
    dependencies {
        compileOnly(project(":api"))
        // standalone-utils from KamiCommon
        compileOnly(project.property("standaloneUtils") as String)
        // Common Dependencies
        @Suppress("UNCHECKED_CAST")
        (rootProject.extra["commonDependencies"] as List<String>).forEach(dependencies::compileOnly)
    }
}

// Disable root project build
tasks.jar.get().enabled = false