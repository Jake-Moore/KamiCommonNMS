dependencies {
    // Unique dependencies for this module
    compileOnly("net.techcable.tacospigot:server:1.8.8-R0.2-REDUCED-KC")

    // Needs to reference a Pre 1.13 abstract class stored in 1.8
    compileOnly(project(":versions:v1_8_R1"))
}
