dependencies {
    // Unique dependencies for this module
    compileOnly("org.spigotmc:spigot-server:1.12.2-R0.1")

    // Needs to reference a Pre 1.13 abstract class stored in 1.8
    compileOnly(project(":versions:v1_8_R1"))
}
