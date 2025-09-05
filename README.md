&nbsp;
> <a href="https://repo.luxiouslabs.net/#browse/browse:maven-releases:com%2Fkamikazejam%2Fkamicommon%2Fspigot-nms"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/25b97e226e0ecf38e0135223111fd115/raw/version.json" /></a> <a href="https://repo.luxiouslabs.net/#browse/browse:maven-releases:com%2Fkamikazejam%2Fkamicommon%2Fspigot-nms"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/25b97e226e0ecf38e0135223111fd115/raw/paperVersion.json" /></a>
> 
> **The Last NMS Update may not match the latest Minecraft Version.**  
> This is okay, as NMS updates are not always required for every Minecraft version.
> 
> Repo: [Luxious Repository](https://repo.luxiouslabs.net/)

# KamiCommonNMS
- The NMS (`net.minecraft.server`) portion of the [KamiCommon](https://github.com/Jake-Moore/KamiCommon) library.
- On August 31, 2024, this library was removed from KamiCommon into its own repository.
- It aims to provide nms access while supporting versions 1.8.X to LATEST

## JavaDoc
- https://docs.jake-moore.dev/KamiCommonNMS/

## Disclaimers
- 1.17+ only officially supports **Paper** as the server software.
- This library requires **Java 21** to be used, as such any version of server jar must be runnable on Java 21 as well.
  - It is highly likely older versions need to be specially compiled for Java 21.

## Transitive Dependencies
This nms project includes a few libraries it needs to compile and enforce cross-version support. They are:
- [com.github.cryptomorin:XSeries](https://github.com/CryptoMorin/XSeries)
- [de.tr7zw:item-nbt-api](https://github.com/tr7zw/Item-NBT-API)
- [com.github.fierioziy.particlenativeapi:ParticleNativeAPI](https://github.com/Fierioziy/ParticleNativeAPI)

These libraries are not shaded. They are defined as **transitive dependencies** for upstream projects to use as they see fit.
- If you already have these on the classpath, you can exclude them in the dependency
- If you plan to shade this library, and you don't exclude them, they will be shaded as well.
  - **just remember to relocate item-nbt-api** (as described on their [wiki](https://github.com/tr7zw/Item-NBT-API/wiki/Using-Gradle#option-2-shading-the-nbt-api-into-your-plugin))

## Using the Library
As a sub-project of KamiCommon, this library is published under that project's package.  
You'll find its code under the package `com.kamikazejam.kamicommon`, and published under `com.kamikazejam.kamicommon:spigot-nms`

### Repository Information
Add the following Repository to your build file.
#### Maven [pom.xml]:
```xml
<repository>
  <id>luxious-public</id>
  <name>Luxious Repository</name>
  <url>https://repo.luxiouslabs.net/repository/maven-public/</url>
</repository>
```
#### Gradle (kotlin) [build.gradle.kts]:
```kotlin
maven {
    name = "luxiousPublic"
    url = uri("https://repo.luxiouslabs.net/repository/maven-public/")
}
```
#### Gradle (groovy) [build.gradle]:
```groovy
maven {
  name "luxiousPublic"
  url "https://repo.luxiouslabs.net/repository/maven-public/"
}
```

### Dependency Information
Add the following dependency to your build file.  
Replace `{VERSION}` with the version listed at the top of this page.  

#### Maven Dependency [pom.xml]
```xml
<dependency>
  <groupId>com.kamikazejam.kamicommon</groupId>
  <artifactId>spigot-nms</artifactId>
  <version>{VERSION}</version>
  <scope>compile</scope>
</dependency>
```

#### Gradle Dependency (groovy) [build.gradle]
```groovy
implementation "com.kamikazejam.kamicommon:spigot-nms:{VERSION}"
```

#### Gradle Dependency (kotlin) [build.gradle.kts]
```kotlin
implementation("com.kamikazejam.kamicommon:spigot-nms:{VERSION}")
```
