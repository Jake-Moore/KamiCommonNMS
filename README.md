&nbsp;
> <a href="https://repo.luxiouslabs.net/#browse/browse:maven-releases:com%2Fkamikazejam%2Fkamicommon%2Fspigot-nms"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/25b97e226e0ecf38e0135223111fd115/raw/version.json" /></a> <a href="https://repo.luxiouslabs.net/#browse/browse:maven-releases:com%2Fkamikazejam%2Fkamicommon%2Fspigot-nms"> <img alt="Latest Release" src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/Jake-Moore/25b97e226e0ecf38e0135223111fd115/raw/paperVersion.json" /></a>
> 
> **The 'Last NMS Update' may not match the latest Minecraft Version.**  
> This is okay, as NMS updates are not always required for every Minecraft version.
> 
> Repo: [Luxious Repository](https://repo.luxiouslabs.net/)

# KamiCommonNMS
- The NMS (`net.minecraft.server`) portion of the [KamiCommon](https://github.com/Jake-Moore/KamiCommon) library.
- On August 31, 2024, this library was removed from KamiCommon into its own repository.
- It aims to provide nms access while supporting versions 1.8.X to LATEST

## JavaDoc
- https://docs.jake-moore.dev/KamiCommonNMS/

## Module layout: which module does a class belong in?

**This convention changed. It used to be the opposite, and the module names read the other way round.**

It used to be that a class lived in the module named for the last version it worked on.
`Teleporter1_8_R3` meant "correct through 1.8.8". `VersionedComponent_1_18_R1` meant "works from 1.8
all the way up to 1.18.1". If the ladder never asked for a module below yours, that implied yours
worked all the way down. The module name was an upper bound.

A class now lives in the module named for the first version it works on. The module name is a lower
bound, and the ladder in `:core` says where it stops.

The reason is the Java floor. Each `versions/*` module now targets the JVM its own Minecraft version
required. Java 8 through 1.16.5, 16 for 1.17, 17 through 1.20.4, 21 from 1.20.5, and `v_latest`
tracks 26.x. Bytecode is forward compatible and never backward: a Java 8 class runs on Java 25, but a
Java 21 class cannot load on Java 8. So a class serving 1.13 upward **must** be compiled at Java 8,
which means it must live in a Java 8 module. Putting it in `v_latest`, as several classes were, makes a 1.13
server fail with `UnsupportedClassVersionError` the moment it touches that provider.

So: **place a class in the earliest module whose server version it works on.** That is the lowest Java
floor it can have, and every later version inherits it for free.

### The floor table

Each module targets the JVM its own Minecraft version required. The build checks this against the
emitted class files, so a module cannot drift from what it declares.

| modules | Java | why |
|---|---|---|
| `api`, `core` | 8 | a 1.8.8 server loads both in full |
| `v1_8_R1` through `v1_16_R3`, `worlds6` | 8 | those versions run on Java 8 |
| `v1_17_R1` | 16 | what 1.17 required |
| `v1_18_R1` through `v1_20_R3` | 17 | what 1.18 through 1.20.4 required |
| `worlds7` | 17 | `worldguard-bukkit:7.0.9` is entirely class-file major 61 |
| `v1_20_CB`, `v1_21_4`, `v1_21_9`, `v1_21_11` | 21 | 1.20.5 onward |
| `v_latest` | 25 | what 26.x requires |

The table lives in `gradle/module-floors.properties`, read by the build and by both floor checks. A
module missing from it fails the build rather than inheriting whatever the build was running.

`v_latest` can target 25 only because every capability a 1.21.11 server reaches has a twin in
`v1_21_11`. Those twins are duplicated on purpose rather than moved, and the reason generalises.

**`v_latest` holds a copy of every implementation a 26.x server runs, and nothing dispatches to those
copies.** They exist to be compiled. The convention above puts a class in the module named for the
earliest version it supports, which is right for dispatch but means the code a 26.x server actually
executes is only ever compiled against an old dev bundle. A Paper API it uses could disappear in 26.x
and the build would not notice until a server did. The `_LATEST` copies compile against
`highestPaperDep`, so bumping that version compile-checks every capability against bleeding-edge
Paper. If a copy stops compiling, that is the finding.

`verifyDispatchFloors` enforces it: any implementation a 26.x server reaches with no `_LATEST` twin
fails the build. Ten had already drifted out before that check existed. The single exemption is
`ItemText`, which throws above 1.16.5 by design, and the check fails if a twin for it ever appears.

Four build tasks keep this honest.

- `verifyFloors` checks every class in the shaded jar against its module's floor, checks that no
  lower-floor class names a higher-floor one, and checks the published metadata against the bytecode.
- `verifyNmsBundles` checks that every module has an adapter, that nothing names one statically, and
  that every capability a ladder asks for is actually implemented rather than inherited from the
  throwing default.
- `verifyDispatchFloors` reads the ladders themselves and checks that no branch sends a server to a
  module its JVM cannot load. **Nothing else can see this.** `verifyFloors` reads bytecode, and
  resolving modules by name means there is no static reference for it to find, so a ladder routing
  1.21.11 into a Java 25 module leaves every other check green.
- `verifyTextFloor` does for `:text` what `verifyFloors` does for the shaded jar.

To see what a running server selected, use `/kc nmsproviders`. It resolves every provider from the
console and prints the implementation each one got.

### Two rules that follow

**1. The ladder in `:core` is the source of truth, not the module name.** A module name now only tells
you where a class *starts*. Where it stops is decided by the `if (ver <= f("..."))` chain in the
provider. Every branch in those ladders carries a comment saying which module it selects and why.
Keep that up, because the layout no longer explains itself.

**2. Moving a class up the ladder means re-checking the branch below it.** If you discover an
implementation needs a newer server than its branch claims and you raise its lower bound, the versions
it vacates fall to the branch beneath, and that branch has never been compiled against them. Verify
it can be, for every version it just absorbed. This is not hypothetical: raising the native
`VersionedComponent` from 1.18.2 to 1.21.4 handed 1.18.2–1.21.3 to the shaded implementation, which
had never been built against anything above 1.18.1.

### Duplication is sometimes correct

Two modules may hold byte-identical code. `ComponentLoggerAdapter_1_16_R3` and the pre-1.18.2 logging
it replaced are one example. That is deliberate. The code is the same because the behaviour is the same;
what differs is the JVM the module targets. Reaching across to share one copy would drag the higher
floor onto the lower server. Fork it, name it for the version it starts at, and say so in a comment.

## Disclaimers
- 1.17+ only officially supports **Paper** as the server software.
- This library runs on whatever Java the server itself requires, down to **Java 8**. See the floor
  table above. Each module targets its own Minecraft version's requirement, so a 1.8.8 server on
  Java 8 loads the library in full.
  - One exception: WorldEdit and WorldGuard 7.x support is compiled at Java 17, so those hooks need
    a Java 17 server even though the rest of the library does not.

## Transitive Dependencies
This nms project includes a few libraries it needs to compile and enforce cross-version support. They are:
- [com.github.cryptomorin:XSeries](https://github.com/CryptoMorin/XSeries)
- [de.tr7zw:item-nbt-api](https://github.com/tr7zw/Item-NBT-API)

> **Removed in 1.2.20:** `ParticleNativeAPI` is no longer a dependency. Nothing in this library used
> it, and its version table stops at 1.21.11, so it cannot work on 26.x. If you relied on receiving
> it transitively, declare it yourself.
>
> **Also in 1.2.20:** XSeries moved from `v13.5.1` to `13.7.1`, which removes `XMaterial.supports(int)`
> in favour of `supports(int, int)`. 13.5.1 throws from `XMaterial`'s static initializer on any 26.x
> server, so the bump is required rather than optional.

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
