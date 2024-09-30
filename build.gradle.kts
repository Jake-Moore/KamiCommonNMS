@Suppress("PropertyName")
var VERSION = "1.0.8"

plugins { // needed for the allprojects section to work
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.2" apply false
    id("io.papermc.paperweight.userdev") version "1.7.3" apply false
}

ext {
    // reduced is just a re-zipped version of the original, without some conflicting libraries
    //  gson, org.json, com.yaml.snakeyaml
    set("lowestSpigotDep", "net.techcable.tacospigot:server:1.8.8-R0.2-REDUCED")    // luxious nexus (public)
    // NOTE: The standalone-utils module must support Java 17 since it's used in nms modules requiring Java 17
    set("standaloneUtils", "com.kamikazejam.kamicommon:standalone-utils:3.6.1.0")
}
extra["commonDependencies"] = listOf(
    "de.tr7zw:item-nbt-api:2.13.2",
    "com.github.cryptomorin:XSeries:11.3.0",
    "com.github.fierioziy.particlenativeapi:ParticleNativeAPI-core:4.3.0"
)

allprojects {
    group = "com.kamikazejam.kamicommon"
    version = VERSION
    description = "KamikazeJAM's nms library for Paper."

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    // Provision Java 17 all subprojects (new modules have version 21 configured)
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

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

    dependencies {
        // Lombok
        compileOnly("org.projectlombok:lombok:1.18.34")
        annotationProcessor("org.projectlombok:lombok:1.18.34")
        testImplementation("org.projectlombok:lombok:1.18.34")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

        // IntelliJ annotations
        compileOnly("org.jetbrains:annotations:25.0.0")
    }

    // We want UTF-8 for everything
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    // Ensure all publish tasks depend on build and shadowJar
    tasks.publish.get().dependsOn(tasks.build)
}

// Disable root project build
tasks.jar.get().enabled = false