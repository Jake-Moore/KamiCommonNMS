package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Version detection and management utility for the KamiCommon NMS system.
 * <p>
 * This class serves as the foundation for all cross-version compatibility in KamiCommon,
 * providing precise Minecraft version detection and standardized version formatting that
 * enables the provider system to select appropriate implementations across 13+ years
 * of Minecraft evolution. It is fundamental to the entire NMS abstraction architecture.
 * </p>
 * <p>
 * <strong>Core Functionality:</strong>
 * <ul>
 * <li><strong>Version Detection:</strong> Extracts precise Minecraft version from Bukkit server</li>
 * <li><strong>Version Formatting:</strong> Converts semantic versions to comparable integers</li>
 * <li><strong>Server Type Detection:</strong> Identifies specialized server implementations</li>
 * <li><strong>Caching:</strong> Optimizes repeated version queries for performance</li>
 * </ul>
 * </p>
 * <p>
 * <strong>How the version is obtained:</strong><br>
 * {@code Server#getMinecraftVersion()} where it exists, reflectively because the 1.8.8 API predates
 * it, falling back to the leading component of {@code Server#getBukkitVersion()}. Package based
 * detection ("vX_XX_RX" from the CraftBukkit package) is <b>not</b> used and has not been reliable
 * since Paper stopped relocating that package in 1.20.5.
 * </p>
 * <p>
 * <strong>Formatted Integer System:</strong><br>
 * Versions are packed into an integer that sorts in release order, so a provider can write
 * {@code if (ver <= f("1.16.5"))} instead of comparing strings. <b>There are two eras</b>, because
 * Minecraft left {@code 1.x} versioning behind after 1.21.11 and moved to calendar versions:
 * <pre>
 * Version                | Integer | Note
 * 1.8                    |    1080 | absent patch reads as 0
 * 1.8.8                  |    1088 |
 * 1.16.5                 |    1165 | hex colour support arrives
 * 1.17.1                 |    1171 |
 * 1.20.4                 |    1204 |
 * 1.21                   |    1210 |
 * 1.21.9                 |    1219 |
 * 1.21.10                |   12110 | FIVE digits, see below
 * 1.21.11                |   12111 | the last 1.x release
 * 26.1.1                 |  260101 | calendar era begins
 * 26.2                   |  260200 | reads as 26|02|00
 * 26.2.build.120-stable  |  260200 | trailing junk is ignored
 * 26.10.1                |  261001 |
 * </pre>
 * </p>
 * <p>
 * <strong>The legacy packing is textual, not arithmetic.</strong> {@code 1.21.10} is
 * {@code "1" + "21" + "10"}, which is 12110 and not 1220. That is why the result is not a fixed
 * width and must never be assumed to be four digits: it is four for most 1.x releases, five once the
 * patch reaches two digits, and six for every calendar version. Compare it, do not slice it.
 * </p>
 * <p>
 * <strong>The two eras cannot collide.</strong> Legacy packing tops out at
 * {@code "1" + "99" + "99"}, which is 19999, and every calendar value is at least 260000. So
 * {@code ver < 260000} is the same question as "is this a 1.x server", and
 * {@code if (ver < f("26")))} is the correct way to write a fallthrough that must not catch 26.x.
 * Prefer that over pinning the last release you happened to know about: a guard written as
 * {@code ver <= f("1.21.11")} silently excludes a future 1.21.12.
 * </p>
 * <p>
 * <strong>Critical Dependencies:</strong><br>
 * This class is used by every provider in the system for version-specific implementation
 * selection. Any changes to version detection logic directly impact the entire NMS
 * abstraction layer and should be thoroughly tested across all supported versions.
 * </p>
 *
 * @see com.kamikazejam.kamicommon.util.nms.NmsVersionParser
 * @see com.kamikazejam.kamicommon.nms.provider.Provider
 */
@SuppressWarnings("unused")
public class NmsVersion {

    /** Cached Minecraft version string to avoid repeated parsing. */
    private static String mcVersion = null;

    /**
     * Retrieves the precise Minecraft version of the current server.
     * <p>
     * This method extracts the semantic version directly from Bukkit's version string,
     * providing the foundation for all version-specific provider selection throughout
     * the NMS system. The result is cached for optimal performance during provider
     * initialization and repeated version checks.
     * </p>
     * <p>
     * <strong>Two sources, in order:</strong>
     * <ol>
     * <li>{@code Server#getMinecraftVersion()}, called reflectively because the 1.8.8 API does not
     *     declare it. This is the authoritative answer where it exists.</li>
     * <li>Otherwise the leading component of {@code Server#getBukkitVersion()}, split on {@code "-"}.</li>
     * </ol>
     * </p>
     * <p>
     * <strong>The fallback is not always semver shaped.</strong> Paper 26.x reports its Bukkit
     * version as {@code "26.2.build.120-stable"}, so after the split the caller can be handed
     * {@code "26.2.build.120"}. {@link com.kamikazejam.kamicommon.util.nms.NmsVersionParser} reads
     * the leading numeric components and ignores the rest, which is why that tolerance exists rather
     * than being cosmetic.
     * </p>
     * <p>
     * <strong>Examples:</strong>
     * <pre>
     * Reported by server              | Extracted MC Version
     * "1.8.8-R0.1-SNAPSHOT"           | "1.8.8"
     * "1.16.5-R0.1-SNAPSHOT"          | "1.16.5"
     * "1.20.4-R0.1-SNAPSHOT"          | "1.20.4"
     * "1.21-R0.1-SNAPSHOT"            | "1.21"
     * "26.2.build.120-stable"         | "26.2.build.120"
     * </pre>
     * </p>
     *
     * @return the Minecraft version string (e.g., "1.8.8", "1.16.5", "1.20.4", "26.2.build.120")
     */
    @SneakyThrows
    public static String getMCVersion() {
        if (mcVersion != null) {return mcVersion;}

        // Prefer Server#getMinecraftVersion(); reflective because the 1.8.8 API lacks it
        String direct = getMinecraftVersionReflectively();
        if (direct != null && !direct.isEmpty()) {
            mcVersion = direct;
            return mcVersion;
        }

        // Not always "1.20.4"-shaped: Paper 26.x reports "26.2.build.120-stable" here
        String bukkitVer = Bukkit.getServer().getBukkitVersion(); // i.e. 1.20.4-R0.1-SNAPSHOT
        mcVersion = bukkitVer.split("-")[0]; // i.e. 1.20.4
        return mcVersion;
    }

    /**
     * @return the value of {@code Server#getMinecraftVersion()}, or null if this server predates it.
     */
    private static @Nullable String getMinecraftVersionReflectively() {
        try {
            Object server = Bukkit.getServer();
            if (server == null) {return null;}
            Method method = server.getClass().getMethod("getMinecraftVersion");
            Object value = method.invoke(server);
            return (value instanceof String) ? (String) value : null;
        } catch (Throwable ignored) {
            return null; // older servers lack the method; getBukkitVersion() covers them
        }
    }

    /** Cached formatted version integer to avoid repeated computation. */
    private static int formattedNms = -1;

    /**
     * Converts the Minecraft version to an integer that sorts in release order.
     * <p>
     * <b>The width is not fixed.</b> Earlier versions of this documentation described a "4-digit
     * integer" with a single patch digit, which was wrong for {@code 1.21.10} and for every calendar
     * version. See the class javadoc for the full table. In short: four digits for most {@code 1.x}
     * releases, five once the patch reaches two digits, six for {@code 26.x} and later. Treat the
     * result as an opaque ordered value. Compare it, do not slice digits out of it or assume a range.
     * </p>
     * <p>
     * <strong>Two eras:</strong>
     * <ul>
     * <li><strong>Legacy ({@code 1.x}):</strong> textual packing, so {@code 1.21.10} is
     *     {@code "1" + "21" + "10"} = 12110. Reproduced digit for digit from the original scheme, so
     *     every {@code f("1.x.y")} threshold that ever existed keeps the value it always had.</li>
     * <li><strong>Calendar ({@code >= 2.x}):</strong> {@code major*10_000 + minor*100 + patch}, so
     *     {@code 26.2} is 260200 and reads as {@code 26|02|00}.</li>
     * </ul>
     * The legacy branch cannot exceed 19999 and the calendar branch starts at 260000, so the two
     * never overlap and no offset is needed.
     * </p>
     * <p>
     * <strong>Writing version guards:</strong>
     * <pre>
     * if (ver &gt;= f("1.16.2")) { ... }   // a feature that arrived in a known release
     * if (ver &lt; f("26"))      { ... }   // everything before the calendar era
     * </pre>
     * Prefer {@code ver &lt; f("26")} to naming the newest release you know of. A fallthrough written
     * as {@code ver &lt;= f("1.21.11")} quietly stops covering a future {@code 1.21.12}.
     * </p>
     * <p>
     * <strong>Performance Note:</strong><br>
     * This value is computed once and cached indefinitely, as the server version
     * cannot change during runtime. All providers rely on this method for version
     * comparisons, making caching essential for optimal performance.
     * </p>
     *
     * @return the formatted NMS version as an order-preserving integer, of variable width
     * @see #getMCVersion()
     * @see com.kamikazejam.kamicommon.util.nms.NmsVersionParser#getFormattedNmsInteger(String)
     */
    public static int getFormattedNmsInteger() {
        if (formattedNms != -1) {return formattedNms;}
        formattedNms = NmsVersionParser.getFormattedNmsInteger(getMCVersion());
        return formattedNms;
    }

    /** Cached WineSpigot detection result to avoid repeated server name queries. */
    private static Boolean isWineSpigot = null;

    /**
     * Determines if the current server is running WineSpigot implementation.
     * <p>
     * WineSpigot is a specialized Minecraft server implementation that may require
     * specific compatibility adjustments or alternative provider implementations.
     * This detection helps the NMS system adapt to specialized server behaviors
     * and ensure optimal compatibility across different server implementations.
     * </p>
     * <p>
     * <strong>Usage in Provider Selection:</strong><br>
     * Some providers may need to adjust their behavior for WineSpigot-specific
     * quirks or optimizations. This method enables conditional logic within
     * provider implementations to handle implementation-specific differences.
     * </p>
     * <p>
     * <strong>Performance Optimization:</strong><br>
     * The result is cached after the first query to avoid repeated string
     * comparisons during provider initialization and version-dependent operations.
     * </p>
     *
     * @return {@code true} if running on WineSpigot, {@code false} otherwise
     */
    public static boolean isWineSpigot() {
        if (isWineSpigot == null) {
            return isWineSpigot = Bukkit.getServer().getName().equals("WineSpigot");
        }
        return isWineSpigot;
    }
}
