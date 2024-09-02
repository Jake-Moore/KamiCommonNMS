# KamiCommonNMS
- The NMS (net.minecraft.server) portion of the [KamiCommon](https://github.com/Jake-Moore/KamiCommon) library.
- On August 31, 2024, this library was removed from KamiCommon into its own repository.
- It aims to support versions: 1.8.X to LATEST

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