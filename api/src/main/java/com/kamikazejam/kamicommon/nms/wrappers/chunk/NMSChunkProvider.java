package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

/**
 * NMS wrapper interface for Minecraft chunk provider objects.
 * <p>
 * This interface provides a version-independent abstraction for working with
 * Minecraft's chunk management system. The chunk provider is responsible for
 * loading, saving, and managing chunks within a world, acting as the bridge
 * between the world and its constituent chunks.
 * </p>
 * <p>
 * The chunk provider manages chunk lifecycle operations and provides factory
 * methods for creating NMS chunk wrappers from Bukkit chunk objects. It
 * maintains the relationship between chunks and their world context while
 * abstracting away version-specific implementation details.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Get chunk provider from world
 * NMSWorld nmsWorld = worldWrapper.getNMSWorld();
 * NMSChunkProvider provider = nmsWorld.getChunkProvider();
 * 
 * // Wrap Bukkit chunk
 * NMSChunk nmsChunk = provider.wrap(bukkitChunk);
 * 
 * // Save chunk modifications
 * provider.saveChunk(nmsChunk);
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public interface NMSChunkProvider extends NMSObject {
    /**
     * Retrieves the NMS world that this chunk provider manages.
     * <p>
     * The chunk provider is associated with a specific world and manages
     * all chunks within that world. This method provides access to the
     * parent world wrapper for context and world-level operations.
     * </p>
     *
     * @return the {@link NMSWorld} that this provider manages chunks for
     */
    @NotNull NMSWorld getNMSWorld();

    /**
     * Checks if force chunk loading is enabled.
     * <p>
     * Force chunk loading determines whether chunks are loaded synchronously
     * when requested, regardless of the server's normal chunk loading policies.
     * This can affect performance but ensures immediate chunk availability.
     * </p>
     * <p>
     * The default implementation returns {@code true} as most versions
     * either support this feature or have it enabled by default.
     * </p>
     *
     * @return {@code true} if force chunk loading is enabled, {@code false} otherwise
     */
    default boolean isForceChunkLoad() {
        // For most versions this is simply true
        return true;
    }

    /**
     * Sets the force chunk loading behavior.
     * <p>
     * This method attempts to configure whether chunks should be loaded
     * synchronously when requested. Not all Minecraft versions support
     * changing this setting, so the default implementation does nothing.
     * </p>
     * <p>
     * Implementations for versions that support this feature should override
     * this method to provide the appropriate functionality.
     * </p>
     *
     * @param value {@code true} to enable force chunk loading, {@code false} to disable
     */
    default void setForceChunkLoad(boolean value) {
        // Do nothing (most versions can't change this)
    }

    /**
     * Saves the specified chunk to persistent storage.
     * <p>
     * This method forces the chunk to be written to disk, ensuring that
     * any modifications made through NMS operations are persisted. This
     * is particularly important after making direct chunk modifications
     * that might not trigger automatic save mechanisms.
     * </p>
     * <p>
     * The save operation may include chunk data, tile entities, and any
     * other persistent chunk state depending on the implementation.
     * </p>
     *
     * @param chunk the {@link NMSChunk} to save to disk
     */
    void saveChunk(@NotNull NMSChunk chunk);

    /**
     * Creates an NMS chunk wrapper for the specified Bukkit chunk.
     * <p>
     * This factory method converts a Bukkit chunk object into its
     * corresponding NMS wrapper, providing access to low-level chunk
     * operations and internal chunk state. The wrapper maintains
     * references to both the NMS and Bukkit representations.
     * </p>
     * <p>
     * The returned wrapper is tied to this chunk provider and world,
     * ensuring proper context and management relationships.
     * </p>
     *
     * @param chunk the Bukkit {@link Chunk} to wrap
     * @return a new {@link NMSChunk} wrapper for the specified chunk
     */
    @NotNull
    NMSChunk wrap(@NotNull Chunk chunk);
}
