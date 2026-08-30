dependencies {
    // Unique dependencies for this module
    compileOnly("org.spigotmc:spigot-server:1.16.5-R0.1")

    // PreSpawnSpawnerAdapter_1_13_R1 needs Paper's PreSpawnerSpawnEvent, which is not in the Spigot
    // server jar above. It used to be compiled against Paper 26.x as part of v_latest, which is how
    // a class dispatched to 1.13 servers ended up needing Java 21 to load.
    compileOnly("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")
}
