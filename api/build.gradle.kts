plugins {
    // Unique plugins for this module
}

dependencies {
    api("de.tr7zw:item-nbt-api:2.13.2")
    api("com.github.cryptomorin:XSeries:11.2.1")
    api("com.github.fierioziy.particlenativeapi:ParticleNativeAPI-core:4.3.0")

    compileOnly(project.property("standaloneUtils") as String) // standalone-utils from KamiCommon
    compileOnly(project.property("lowestSpigotDep") as String)
}