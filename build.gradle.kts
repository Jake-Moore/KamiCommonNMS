@Suppress("PropertyName")
val VERSION = "1.2.24" // -SNAPSHOT marks WIP versions (snapshots are not CI published)

plugins {
    id("com.gradleup.shadow") version "9.6.1" apply false
    // beta.23's own task classes are Java 21, and paperweight runs its workers in each module's
    //  toolchain JVM. That is fine because no module's toolchain is below 21: see the TOOLCHAIN
    //  note in versions/build.gradle.kts, where the toolchain is deliberately not the floor.
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.23" apply false
}

val highestPaperDep = "26.2.build.121-stable"

// Testing server APIs (the earliest supported version, and the latest PaperMC version)
val oldestServerAPI = "net.techcable.tacospigot:server:1.8.8-R0.2-REDUCED-KC"
val newestServerAPI = "io.papermc.paper:paper-api:$highestPaperDep"

ext {
    // Use ENV variable to switch between oldest and newest API for testing
    val useNewestAPI: String? = System.getenv("MC_SERVER_NEWEST_API")
    val testingServerAPI = if (useNewestAPI != null && useNewestAPI.equals("true", true)) {
        newestServerAPI
    } else {
        oldestServerAPI
    }

    // Run Configurations can be set up to supply alternate server API dependencies for quick compilation testing.
    //  This is used by the `api` and `core` modules which just use generic version-independent server APIs
    val serverVersionAPI = System.getenv("MC_SERVER_API") ?: testingServerAPI
    set("serverAPI", serverVersionAPI)

    // project property exposing the highest paper version currently verified to work
    //   the project may support newer versions, but we have not tested or compiled against them yet
    set("highestPaperDep", highestPaperDep)
    // NOTE: The standalone-utils module must support Java 17 since it's used in nms modules requiring Java 17
    set("standaloneUtils", "com.kamikazejam.kamicommon:standalone-utils:5.0.0-alpha.40")
    // Lombok Dependency
    set("lombokDep", "org.projectlombok:lombok:1.18.46")
    set("jetbrainsDep", "org.jetbrains:annotations:26.1.0")
    set("adventureDep", ":text")
}
extra["commonDependencies"] = listOf(
    // 2.15.3's MinecraftVersion stops at MC1_21_R6; 2.16.0 adds MC26_1 and MC26_2.
    "de.tr7zw:item-nbt-api:2.16.0",
    // 13.5.1 reads only a single-digit major and throws from XMaterial's <clinit> on 26.x.
    "com.github.cryptomorin:XSeries:13.8.0",
)

allprojects {
    group = "com.kamikazejam.kamicommon"
    version = VERSION
    description = "KamikazeJAM's nms library for Paper."

    tasks.withType<Javadoc> {
        (options as StandardJavadocDocletOptions).apply {
            encoding = "UTF-8"
            charSet = "UTF-8"
        }
    }
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        // PaperMC & SpigotMC
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        // Luxious Nexus
        maven("https://repo.luxiouslabs.net/repository/maven-public/")
        // Spigot Plugin Repos
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://mvn.lumine.io/repository/maven-public/") {
            content {
                includeGroup("io.lumine")
                excludeGroup("org.jetbrains")
            }
        }
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        // Misc repos
        maven("https://jitpack.io")
        gradlePluginPortal()
    }

    // We want UTF-8 for everything
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}

tasks.register("printPaperVersion") {
    doLast {
        println(highestPaperDep)
    }
}

tasks.register("printOldestServerAPI") {
    doLast {
        println(oldestServerAPI)
    }
}
tasks.register("printNewestServerAPI") {
    doLast {
        println(newestServerAPI)
    }
}

tasks.register("printOldestServerAPI-short") {
    val version = oldestServerAPI.split(":").lastOrNull()
        ?: throw GradleException("Invalid oldestServerAPI format")
    val semver = version.split("-").firstOrNull()
        ?: throw GradleException("Invalid oldestServerAPI version format")
    doLast {
        println(semver)
    }
}

tasks.register("printNewestServerAPI-short") {
    val version = newestServerAPI.split(":").lastOrNull()
        ?: throw GradleException("Invalid newestServerAPI format")
    val semver = version.split("-").firstOrNull()
        ?: throw GradleException("Invalid newestServerAPI version format")
    doLast {
        println(semver)
    }
}