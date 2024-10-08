repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // Unique dependencies for this module
    // Use 1.13 since WorldGuard7 uses BlockData and needs it to compile
    compileOnly("org.spigotmc:spigot-server:1.13.2-R0.1")

    // WorldEdit v7 / World Guard v7
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    compileOnly("com.sk89q.worldedit:fawe:2.4.5")
}
