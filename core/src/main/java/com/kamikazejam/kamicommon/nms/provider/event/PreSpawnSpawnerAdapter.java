package com.kamikazejam.kamicommon.nms.provider.event;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundles;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific spawner event adapters across different server implementations.
 * <p>
 * This provider automatically detects the current Minecraft version and server implementation
 * to return the appropriate spawner event listener. Different server forks have implemented
 * spawner pre-spawn events at different times and with different APIs, requiring version-specific
 * adapters to provide consistent functionality.
 * </p>
 * <p>
 * The adapter system handles the complex landscape of spawner events across different
 * server implementations:
 * <ul>
 * <li><strong>TacoSpigot 1.8.8:</strong> Introduced {@code SpawnerPreSpawnEvent} early</li>
 * <li><strong>Paper 1.9-1.12:</strong> Uses {@code SpawnerSpawnEvent} for pre-spawn detection</li>
 * <li><strong>PaperSpigot 1.13+:</strong> Standardized on {@code PreSpawnerSpawnEvent}</li>
 * </ul>
 * </p>
 * <p>
 * Each adapter implements the same {@link org.bukkit.event.Listener} interface but handles
 * different underlying events, providing a unified {@link com.kamikazejam.kamicommon.nms.abstraction.event.PreSpawnSpawnerEvent}
 * abstraction for plugin developers.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Register the appropriate spawner adapter for the current server version
 * Listener spawnerAdapter = PreSpawnSpawnerAdapter.getSpawnerAdapter();
 * Bukkit.getPluginManager().registerEvents(spawnerAdapter, plugin);
 * 
 * // The adapter will automatically convert server-specific events to unified events
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public class PreSpawnSpawnerAdapter {
    /**
     * Retrieves the appropriate spawner event adapter for the current server version and implementation.
     * <p>
     * This method analyzes the current Minecraft version to determine which server implementation
     * is likely being used and returns the corresponding event adapter. The detection logic
     * accounts for the historical evolution of spawner events across different server forks.
     * </p>
     * <p>
     * Version detection logic:
     * <ol>
     * <li><strong>Minecraft 1.8.8:</strong> Assumes TacoSpigot fork, which introduced early spawner events</li>
     * <li><strong>Minecraft 1.9-1.12.2:</strong> Assumes Paper fork with {@code SpawnerSpawnEvent}</li>
     * <li><strong>Minecraft 1.13+:</strong> Assumes modern PaperSpigot with {@code PreSpawnerSpawnEvent}</li>
     * </ol>
     * </p>
     * <p>
     * Each returned adapter will listen for the appropriate server-specific spawner events
     * and convert them into the unified {@link com.kamikazejam.kamicommon.nms.abstraction.event.PreSpawnSpawnerEvent}
     * that can be handled consistently across all versions.
     * </p>
     *
     * @return a {@link Listener} that adapts server-specific spawner events to the unified API
     */
    @NotNull
    public static Listener getSpawnerAdapter() {
        int nmsVersion = NmsVersion.getFormattedNmsInteger();

        // 1.8.8      -> Assume TacoSpigot fork and use SpawnerPreSpawnEvent
        if (nmsVersion == f("1.8.8")) {
            return NmsBundles.forModule("v1_8_R3").preSpawnSpawnerAdapter();
        }

        // < 1.13     -> Assume Paper fork and use SpawnerSpawnEvent
        if (nmsVersion <= f("1.12.2")) {
            return NmsBundles.forModule("v1_12_R1").preSpawnSpawnerAdapter();
        }

        // 1.13+      -> Assume PaperSpigot and use PreSpawnerSpawnEvent
        // PreSpawnerSpawnEvent has been Bukkit/Paper API since 1.13, so one implementation covers
        // everything above and it lives in v1_13_R1 at Java 8, not v_latest at Java 21.
        return NmsBundles.forModule("v1_13_R1").preSpawnSpawnerAdapter();
    }

    /**
     * Convenience method to convert Minecraft version strings to formatted integers.
     * <p>
     * This helper method converts Minecraft version strings to the same formatted
     * integer format used by {@link NmsVersion#getFormattedNmsInteger()} for
     * version comparison operations within the detection logic.
     * </p>
     *
     * @param mcVersion the Minecraft version string to convert (e.g., "1.8.8", "1.12.2")
     * @return the formatted integer representation of the version
     */
    private static int f(@NotNull String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }
}
