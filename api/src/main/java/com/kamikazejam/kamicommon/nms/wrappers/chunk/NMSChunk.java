package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * NMS wrapper interface for Minecraft chunk objects.
 * <p>
 * This interface provides a version-independent abstraction for working with
 * Minecraft's internal chunk representation. Chunks are 16x16 block columns
 * that extend from the world's minimum height to maximum height, divided
 * into sections for efficient storage and processing.
 * </p>
 * <p>
 * The wrapper provides access to chunk sections, tile entities, and chunk
 * management operations while hiding the complexity of version-specific
 * NMS implementations. It maintains references to both the underlying NMS
 * chunk object and its associated Bukkit representation.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Get NMS chunk wrapper
 * NMSChunk nmsChunk = chunkProvider.wrap(bukkitChunk);
 * 
 * // Work with chunk sections
 * NMSChunkSection section = nmsChunk.getSection(64); // Y level 64
 * section.setType(0, 0, 0, Material.STONE);
 * 
 * // Update clients
 * nmsChunk.sendUpdatePacket(player);
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public interface NMSChunk extends NMSObject {
    /**
     * Retrieves the chunk provider that manages this chunk.
     * <p>
     * The chunk provider is responsible for chunk loading, saving, and
     * management operations. It maintains the relationship between chunks
     * and their world context.
     * </p>
     *
     * @return the {@link NMSChunkProvider} that manages this chunk
     */
    @NotNull NMSChunkProvider getNMSChunkProvider();

    /**
     * Retrieves the Bukkit chunk representation of this NMS chunk.
     * <p>
     * This method provides access to the Bukkit API representation of
     * the same chunk, allowing for integration with Bukkit-based code
     * and plugins that expect Bukkit chunk objects.
     * </p>
     *
     * @return the Bukkit {@link Chunk} representation
     */
    @NotNull Chunk getBukkitChunk();

    /**
     * Retrieves the chunk section at the specified Y coordinate.
     * <p>
     * Chunk sections are 16x16x16 block cubes that divide chunks vertically.
     * The Y coordinate should be the section's base Y level (typically
     * multiples of 16). If no section exists at the specified Y level,
     * this method's behavior is implementation-dependent.
     * </p>
     *
     * @param y the Y coordinate of the section's base level
     * @return the {@link NMSChunkSection} at the specified Y level
     */
    @NotNull
    NMSChunkSection getSection(int y);

    /**
     * Retrieves or creates the chunk section at the specified Y coordinate.
     * <p>
     * This method ensures that a chunk section exists at the specified Y
     * level, creating one if necessary. This is useful when placing blocks
     * in previously empty sections or when working with newly generated chunks.
     * </p>
     *
     * @param y the Y coordinate of the section's base level
     * @return the existing or newly created {@link NMSChunkSection}
     */
    @NotNull
    NMSChunkSection getOrCreateSection(int y);

    /**
     * Removes all tile entities from this chunk.
     * <p>
     * Tile entities represent blocks with complex state (chests, furnaces,
     * signs, etc.). This method clears all such entities from the chunk,
     * which is useful for chunk reset operations or when replacing all
     * blocks in a chunk.
     * </p>
     * <p>
     * <strong>Warning:</strong> This operation cannot be undone and will
     * permanently remove all tile entity data in the chunk.
     * </p>
     */
    void clearTileEntities();

    /**
     * Sends a chunk update packet to the specified player.
     * <p>
     * This method forces the client to update their view of this chunk,
     * ensuring that any changes made through NMS operations are visible
     * to the player. This is typically necessary after direct chunk
     * modifications that bypass Bukkit's change notification system.
     * </p>
     *
     * @param player the {@link Player} to send the update packet to
     */
    void sendUpdatePacket(@NotNull Player player);

    /**
     * Retrieves the X coordinate of this chunk in chunk coordinates.
     * <p>
     * Chunk coordinates are the chunk's position in the world grid,
     * where each unit represents a 16x16 block area. This is different
     * from block coordinates, which represent individual block positions.
     * </p>
     *
     * @return the X coordinate in chunk coordinates
     */
    int getX();

    /**
     * Retrieves the Z coordinate of this chunk in chunk coordinates.
     * <p>
     * Chunk coordinates are the chunk's position in the world grid,
     * where each unit represents a 16x16 block area. This is different
     * from block coordinates, which represent individual block positions.
     * </p>
     *
     * @return the Z coordinate in chunk coordinates
     */
    int getZ();

    /**
     * Saves the chunk to disk and refreshes it for all players.
     * <p>
     * This method performs a complete save and refresh cycle for the chunk,
     * ensuring that all changes are persisted to disk and that all players
     * see the updated chunk state. The implementation may vary between
     * versions but typically involves marking the chunk as dirty, saving
     * it, and sending update packets to relevant players.
     * </p>
     * <p>
     * This operation is useful after making extensive modifications to
     * a chunk to ensure data persistence and visual consistency.
     * </p>
     */
    void saveAndRefresh();

}
