package com.kamikazejam.kamicommon.nms;

import org.bukkit.Bukkit;

/**
 * Lightweight logging utility for the KamiCommon NMS module.
 * <p>
 * This utility provides a simple logging interface specifically designed for the NMS
 * module when direct access to the main KamiCommon logger is not available. It serves
 * as a bridge to Bukkit's logging system while maintaining consistent message formatting
 * and identification for all KamiCommon-related log entries.
 * </p>
 * <p>
 * The logger automatically prefixes all messages with "[KamiCommon]" to ensure
 * clear identification of the source, making it easier to track NMS-related
 * operations in server logs and debug complex cross-version compatibility issues.
 * </p>
 * <p>
 * This implementation is particularly useful during:
 * <ul>
 * <li>NMS version detection and provider initialization</li>
 * <li>Cross-version compatibility warnings and errors</li>
 * <li>Performance-critical operations that need minimal logging overhead</li>
 * <li>Debug information for complex NMS wrapper operations</li>
 * </ul>
 * </p>
 */
@SuppressWarnings("unused")
public class Logger {
    
    /**
     * Logs an informational message to the server console.
     * <p>
     * This method is used for general operational information, version detection
     * results, successful provider initialization, and other non-critical status
     * updates that help administrators understand system behavior.
     * </p>
     *
     * @param s the message to log at INFO level
     */
    public static void info(String s) {
        // Don't have access to KamiCommon specifically in this module, this works well enough
        Bukkit.getLogger().info("[KamiCommon] " + s);
    }

    /**
     * Logs a warning message to the server console.
     * <p>
     * This method is used for non-fatal issues that may affect functionality,
     * such as version compatibility concerns, deprecated API usage, fallback
     * implementations being used, or potential performance implications.
     * </p>
     *
     * @param s the message to log at WARNING level
     */
    public static void warning(String s) {
        // Don't have access to KamiCommon specifically in this module, this works well enough
        Bukkit.getLogger().warning("[KamiCommon] " + s);
    }

    /**
     * Logs a severe error message to the server console.
     * <p>
     * This method is used for critical errors that may prevent proper NMS
     * functionality, such as unsupported Minecraft versions, failed provider
     * initialization, or reflection errors that could compromise system stability.
     * </p>
     *
     * @param s the message to log at SEVERE level
     */
    public static void severe(String s) {
        // Don't have access to KamiCommon specifically in this module, this works well enough
        Bukkit.getLogger().severe("[KamiCommon] " + s);
    }
}
