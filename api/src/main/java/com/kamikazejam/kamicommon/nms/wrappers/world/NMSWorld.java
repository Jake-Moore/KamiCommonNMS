package com.kamikazejam.kamicommon.nms.wrappers.world;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunkProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * NMS wrapper interface for Minecraft world objects.
 * <p>
 * This interface provides a version-independent abstraction for working with
 * Minecraft's internal world representation. It bridges the gap between
 * Bukkit's high-level world API and the underlying NMS world implementation,
 * enabling direct access to advanced world operations and internal state.
 * </p>
 * <p>
 * The wrapper provides access to chunk management, world boundaries, block
 * operations, and entity spawning while maintaining compatibility across
 * different Minecraft versions. It serves as the primary entry point for
 * world-level NMS operations.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Get NMS world wrapper
 * NMSWorld nmsWorld = worldProvider.wrap(bukkitWorld);
 * 
 * // Access chunk operations
 * NMSChunkProvider chunkProvider = nmsWorld.getChunkProvider();
 * NMSChunk chunk = chunkProvider.wrap(bukkitChunk);
 * 
 * // Perform world operations
 * nmsWorld.refreshBlockAt(player, x, y, z);
 * Entity entity = nmsWorld.spawnEntity(location, Zombie.class, SpawnReason.CUSTOM);
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public interface NMSWorld extends NMSObject {
    /**
     * Retrieves the Bukkit world representation of this NMS world.
     * <p>
     * This method provides access to the Bukkit API representation of
     * the same world, allowing for integration with Bukkit-based code
     * and plugins that expect Bukkit world objects.
     * </p>
     *
     * @return the Bukkit {@link World} representation
     */
    @NotNull
    World getBukkitWorld();

    /**
     * Retrieves the chunk provider for this world.
     * <p>
     * The chunk provider manages all chunk operations within this world,
     * including loading, saving, and wrapping chunks for NMS access.
     * It serves as the factory for creating {@link com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunk}
     * wrappers from Bukkit chunks.
     * </p>
     *
     * @return the {@link NMSChunkProvider} for this world
     */
    @NotNull
    NMSChunkProvider getChunkProvider();

    /**
     * Retrieves the minimum height (Y coordinate) for this world.
     * <p>
     * This value represents the lowest Y coordinate where blocks can exist
     * in the world. In older versions, this was typically 0, but newer
     * versions support negative Y coordinates and extended world height.
     * </p>
     * <p>
     * This value is essential for chunk section calculations and ensuring
     * operations remain within valid world boundaries.
     * </p>
     *
     * @return the minimum Y coordinate for this world
     */
    int getMinHeight();

    /**
     * Retrieves the maximum height (Y coordinate) for this world.
     * <p>
     * This value represents the highest Y coordinate where blocks can exist
     * in the world. The maximum height varies between Minecraft versions
     * and world types, with newer versions supporting extended heights.
     * </p>
     * <p>
     * This value is essential for chunk section calculations and ensuring
     * operations remain within valid world boundaries.
     * </p>
     *
     * @return the maximum Y coordinate for this world
     */
    int getMaxHeight();

    /**
     * Forces a block refresh for a specific player at the given coordinates.
     * <p>
     * This method sends a block update packet to the specified player,
     * ensuring they see the current state of the block at the given
     * coordinates. This is particularly useful after making direct
     * block modifications through NMS that might not trigger automatic
     * client updates.
     * </p>
     * <p>
     * The refresh only affects the specified player's view and does not
     * modify the actual block state or notify other players.
     * </p>
     *
     * @param player the {@link Player} to send the block update to
     * @param x the X coordinate of the block to refresh
     * @param y the Y coordinate of the block to refresh
     * @param z the Z coordinate of the block to refresh
     */
    void refreshBlockAt(@NotNull Player player, int x, int y, int z);

    /**
     * Spawns an entity at the specified location with the given spawn reason.
     * <p>
     * This method provides direct access to NMS entity spawning, allowing
     * for more control over the spawning process than the standard Bukkit
     * API. The spawn reason affects various game mechanics and plugin
     * event handling.
     * </p>
     * <p>
     * The entity is spawned using NMS methods, which may bypass some
     * Bukkit event systems or provide different behavior compared to
     * standard Bukkit spawning methods.
     * </p>
     *
     * @param <T> the type of entity to spawn
     * @param loc the {@link Location} where the entity should be spawned
     * @param clazz the {@link Class} representing the entity type to spawn
     * @param reason the {@link CreatureSpawnEvent.SpawnReason} for this spawn event
     * @return the newly spawned entity instance
     */
    @NotNull
    <T extends org.bukkit.entity.Entity> T spawnEntity(@NotNull Location loc, @NotNull Class<T> clazz, @NotNull CreatureSpawnEvent.SpawnReason reason);

    /**
     * Retrieves the block utility for this world.
     * <p>
     * The block utility provides high-performance block manipulation
     * methods that are optimized for bulk operations and direct NMS
     * access. This is an internal API method used by other components
     * of the NMS abstraction layer.
     * </p>
     * <p>
     * <strong>Note:</strong> This method is intended for internal use
     * by other NMS wrapper components and should generally not be
     * accessed directly by external code.
     * </p>
     *
     * @return the {@link AbstractBlockUtil} for this world
     */
    @NotNull
    AbstractBlockUtil getBlockUtil();
}
