package com.kamikazejam.kamicommon.nms.library.worldedit;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Version detection and integration hook for WorldEdit plugin.
 * <p>
 * This hook provides automatic detection and initialization of version-specific
 * WorldEdit API implementations, supporting both WorldEdit and FastAsyncWorldEdit
 * (FAWE) installations. It abstracts away the differences between WorldEdit 6
 * and WorldEdit 7 APIs while providing a unified interface for schematic operations.
 * </p>
 * <p>
 * The hook uses lazy initialization to detect the installed WorldEdit version
 * and create the appropriate API wrapper only when first accessed. This approach
 * ensures compatibility with servers that may not have WorldEdit installed
 * while providing full functionality when it is available.
 * </p>
 * <p>
 * Version detection strategy:
 * <ol>
 * <li>Check for WorldEdit plugin and use version string for detection</li>
 * <li>If WorldEdit not found, check for FastAsyncWorldEdit</li>
 * <li>Use reflection to detect WorldEdit 7 classes within FAWE</li>
 * <li>Fall back to WorldEdit 6 compatibility if detection fails</li>
 * </ol>
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Check if WorldEdit is available
 * WorldEditApi<Clipboard> api = WorldEditHook.get();
 * if (api != null) {
 *     // Perform WorldEdit operations
 *     Clipboard clipboard = api.getClipboardByFile(schematicFile);
 *     api.pasteClipboard(world, location, clipboard, transform);
 * }
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public class WorldEditHook {
    /**
     * The cached WorldEdit API instance for the detected version.
     * <p>
     * This field holds the version-specific WorldEdit API implementation
     * after first initialization. The instance is created once and reused
     * for all subsequent calls to avoid repeated version detection overhead.
     * </p>
     */
    private static WorldEditApi<Clipboard> worldEditVer = null;

    /**
     * Retrieves the WorldEdit API instance for the installed version.
     * <p>
     * This method performs lazy initialization of the WorldEdit API, detecting
     * the installed version and creating the appropriate implementation on first
     * access. Subsequent calls return the cached instance for optimal performance.
     * </p>
     * <p>
     * The method supports multiple WorldEdit distributions:
     * <ul>
     * <li>Standard WorldEdit 6.x installations</li>
     * <li>Standard WorldEdit 7.x installations</li>
     * <li>FastAsyncWorldEdit (FAWE) with embedded WorldEdit 6 or 7</li>
     * <li>Standalone FAWE installations without WorldEdit plugin</li>
     * </ul>
     * </p>
     * <p>
     * Version detection is performed using plugin version strings and reflection
     * to ensure compatibility across different server configurations and WorldEdit
     * distributions.
     * </p>
     *
     * @return the {@link WorldEditApi} instance for the detected version, or {@code null} if WorldEdit is not available
     */
    @Nullable
    public static WorldEditApi<Clipboard> get() {
        if (worldEditVer != null) {
            return worldEditVer;
        }

        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null || !plugin.isEnabled()) { return null; }

        String ver = plugin.getDescription().getVersion();
        if (ver.startsWith("6")) {
            return worldEditVer = new WorldEdit6();
        } else if (ver.startsWith("7")) {
            return worldEditVer = new WorldEdit7();
        }

        // Try to fetch FAWE (for later versions WorldEdit is not required to run FAWE)
        // So if we find FAWE without WE, we can assume WE 7
        Plugin fawe = Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
        if (fawe != null && fawe.isEnabled()) {
            try {
                // If this class is found, it's v7 WorldEdit within FAWE
                Class<?> v7Specific = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                return worldEditVer = new WorldEdit7();
            }catch (Throwable ignored) {
                // If we have an error, try falling back to WE 6
                return worldEditVer = new WorldEdit6();
            }
        }

        return null;
    }
}
