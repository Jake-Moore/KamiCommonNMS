package com.kamikazejam.kamicommon.nms.library.worldguard;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Version detection and integration hook for WorldGuard plugin.
 * <p>
 * This hook provides automatic detection and initialization of version-specific
 * WorldGuard API implementations, supporting both WorldGuard 6 and WorldGuard 7
 * installations. It abstracts away the significant API differences between these
 * versions while providing a unified interface for region operations, PvP queries,
 * and player tracking.
 * </p>
 * <p>
 * The hook uses lazy initialization to detect the installed WorldGuard version
 * and create the appropriate API wrapper only when first accessed. This approach
 * ensures compatibility with servers that may not have WorldGuard installed
 * while providing full functionality when it is available.
 * </p>
 * <p>
 * WorldGuard version differences:
 * <ul>
 * <li>WorldGuard 6: Legacy API with different region management and query systems</li>
 * <li>WorldGuard 7: Modern API with improved region handling and new query methods</li>
 * </ul>
 * </p>
 * <p>
 * The hook integrates with the NMS world wrapper system to provide consistent
 * world access patterns across different Minecraft and WorldGuard versions.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Check if WorldGuard is available
 * WorldGuardApi api = WorldGuardHook.get();
 * if (api != null) {
 *     // Perform WorldGuard operations
 *     boolean canPvP = api.canPVP(player, location);
 *     boolean canBuild = api.playerCanBuild(player, location);
 *     Set<Player> playersInRegion = api.getPlayersInRegion(world, regionName);
 * }
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public class WorldGuardHook {
    /**
     * The cached WorldGuard API instance for the detected version.
     * <p>
     * This field holds the version-specific WorldGuard API implementation
     * after first initialization. The instance is created once and reused
     * for all subsequent calls to avoid repeated version detection overhead.
     * </p>
     */
    private static @Nullable WorldGuardApi worldguard = null;

    /**
     * Retrieves the WorldGuard API instance for the installed version.
     * <p>
     * This method performs lazy initialization of the WorldGuard API, detecting
     * the installed version and creating the appropriate implementation on first
     * access. Subsequent calls return the cached instance for optimal performance.
     * </p>
     * <p>
     * Version detection is performed using the plugin version string, supporting
     * both WorldGuard 6.x and WorldGuard 7.x installations. The method integrates
     * with the NMS world wrapper system to provide consistent world access across
     * different Minecraft versions.
     * </p>
     * <p>
     * If WorldGuard is not installed, not enabled, or an unsupported version
     * is detected, the method returns {@code null} to indicate that WorldGuard
     * functionality is not available.
     * </p>
     *
     * @return the {@link WorldGuardApi} instance for the detected version, or {@code null} if WorldGuard is not available
     */
    @Nullable
    public static WorldGuardApi get() {
        if (worldguard != null) {
            return worldguard;
        }

        // Ensure WorldGuard is enabled
        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (wg == null || !wg.isEnabled()) { return null; }

        // Check for supported versions
        String ver = wg.getDescription().getVersion();
        if (ver.startsWith("6")) {
            return worldguard = new WorldGuard6(wg, NmsAPI.getNmsWorldWrapper());
        }else if (ver.startsWith("7")) {
            return worldguard = new WorldGuard7(wg, NmsAPI.getNmsWorldWrapper());
        }else {
            return null;
        }
    }
}
