plugins {
    // Unique plugins for this module
}

dependencies {
    api("de.tr7zw:item-nbt-api:2.13.2")
    api("com.github.cryptomorin:XSeries:11.2.1")
    api("com.github.fierioziy.particlenativeapi:ParticleNativeAPI-core:4.3.0")
    // standalone-utils from KamiCommon
    api("com.kamikazejam.kamicommon:standalone-utils:3.5.0.8-20240831.071506-1")

    compileOnly(project.property("lowestSpigotDep") as String)
}