// Give all versions access to api and standalone-utils
subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    // Provision Java 17 all subprojects (new modules have version 21 configured)
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
    }
}