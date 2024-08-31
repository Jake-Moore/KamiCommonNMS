// Give all versions access to api and standalone-utils
subprojects {
    dependencies {
        compileOnly(project(":api"))
        compileOnly(project.property("standaloneUtils") as String) // standalone-utils from KamiCommon
    }
}

// Disable root project build
tasks.jar.get().enabled = false
tasks.shadowJar.get().enabled = false