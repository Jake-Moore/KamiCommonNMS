package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

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
 * <strong>Version Format Evolution:</strong><br>
 * This system adapts to Minecraft's changing version identification methods:
 * <ul>
 * <li><strong>Legacy (1.8-1.12):</strong> Uses traditional "vX_XX_RX" package-based detection</li>
 * <li><strong>Modern (1.13+):</strong> Direct Minecraft version parsing from Bukkit version</li>
 * <li><strong>Post-Relocation (1.17+):</strong> CB package relocation no longer reliable</li>
 * <li><strong>Mojang-Mapped (1.20.5+):</strong> New naming conventions require version-based selection</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Formatted Integer System:</strong><br>
 * The formatted integer system enables efficient version comparisons across providers:
 * <pre>
 * Version    | Integer | Usage
 * 1.8.9      | 1089    | Legacy comparison base
 * 1.13.2     | 1132    | Flattening update detection
 * 1.16.5     | 1165    | Hex color support detection
 * 1.17.1     | 1171    | NMS restructure detection
 * 1.20.4     | 1204    | Modern API transition
 * 1.21.0     | 1210    | Mojang mapping detection
 * </pre>
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
     * <strong>Version Evolution Context:</strong><br>
     * Modern Minecraft servers (1.13+) use semantic versioning in the Bukkit version
     * string, making direct version extraction more reliable than legacy package-based
     * detection methods. This approach ensures compatibility with future server
     * implementations and specialized forks.
     * </p>
     * <p>
     * <strong>Examples:</strong>
     * <pre>
     * Bukkit Version String        | Extracted MC Version
     * "1.8.8-R0.1-SNAPSHOT"       | "1.8.8"
     * "1.16.5-R0.1-SNAPSHOT"      | "1.16.5"
     * "1.20.4-R0.1-SNAPSHOT"      | "1.20.4"
     * "1.21-R0.1-SNAPSHOT"        | "1.21"
     * </pre>
     * </p>
     *
     * @return the Minecraft version string (e.g., "1.8.8", "1.16.5", "1.20.4")
     */
    @SneakyThrows
    public static String getMCVersion() {
        if (mcVersion != null) {return mcVersion;}

        String bukkitVer = Bukkit.getServer().getBukkitVersion(); // i.e. 1.20.4-R0.1-SNAPSHOT
        mcVersion = bukkitVer.split("-")[0]; // i.e. 1.20.4
        return mcVersion;
    }

    /** Cached formatted version integer to avoid repeated computation. */
    private static int formattedNms = -1;

    /**
     * Converts the Minecraft version to a standardized 4-digit integer for efficient comparison.
     * <p>
     * This method transforms semantic version strings into comparable integers using a
     * standardized format: major[1]minor[2]patch[1]. This enables efficient version
     * range checking across all providers and supports the complex version logic
     * required for 13+ years of Minecraft compatibility.
     * </p>
     * <p>
     * <strong>Format Specification:</strong><br>
     * The integer format allocates digits as follows:
     * <ul>
     * <li><strong>Major:</strong> 1 digit (covers versions 1.x)</li>
     * <li><strong>Minor:</strong> 2 digits (handles 1.8 through 1.21+)</li>
     * <li><strong>Patch:</strong> 1 digit (accommodates point releases)</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Version Comparison Examples:</strong>
     * <pre>
     * Version | Integer | Provider Selection Use Case
     * 1.8.9   | 1089    | if (ver >= 1089) { ... }
     * 1.13.0  | 1130    | if (ver >= 1130) { ... }
     * 1.16.2  | 1162    | if (ver >= 1162) { ... }
     * 1.17.0  | 1170    | if (ver >= 1170) { ... }
     * 1.20.5  | 1205    | if (ver >= 1205) { ... }
     * </pre>
     * </p>
     * <p>
     * <strong>Performance Note:</strong><br>
     * This value is computed once and cached indefinitely, as the server version
     * cannot change during runtime. All providers rely on this method for version
     * comparisons, making caching essential for optimal performance.
     * </p>
     *
     * @return the formatted NMS version as a 4-digit integer
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
