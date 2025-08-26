package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.util.data.XBlockData;
import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * NMS wrapper interface for Minecraft chunk section objects.
 * <p>
 * This interface provides a version-independent abstraction for working with
 * chunk sections, which are 16x16x16 block cubes that make up chunks. Chunk
 * sections are the fundamental storage units for blocks in Minecraft, providing
 * efficient storage and manipulation of block data within chunks.
 * </p>
 * <p>
 * Each chunk section covers a 16-block-high slice of a chunk, with sections
 * stacked vertically from the world's minimum height to maximum height.
 * Sections can be empty (containing only air) or populated with various
 * block types and states.
 * </p>
 * <p>
 * The wrapper provides direct block manipulation within the section coordinate
 * space (0-15 for X and Z, section-relative for Y) and abstracts away the
 * complexity of version-specific block storage formats.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Get chunk section
 * NMSChunkSection section = chunk.getOrCreateSection(64);
 * 
 * // Set blocks within the section
 * section.setType(0, 0, 0, Material.STONE);
 * section.setType(15, 15, 15, customBlockData);
 * 
 * // Check if section is empty
 * if (!section.isEmpty()) {
 *     // Process non-empty section
 * }
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public interface NMSChunkSection extends NMSObject {
    /**
     * Retrieves the parent chunk that contains this section.
     * <p>
     * This method provides access to the chunk that this section belongs to,
     * enabling access to chunk-level operations and maintaining the
     * hierarchical relationship between chunks and their sections.
     * </p>
     *
     * @return the {@link NMSChunk} that contains this section
     */
    @NotNull
    NMSChunk getNMSChunk();

    /**
     * Sets the block type at the specified coordinates within this section.
     * <p>
     * This method places a block of the specified material at the given
     * coordinates within the section. Coordinates are relative to the
     * section (0-15 for X and Z, section-relative for Y).
     * </p>
     * <p>
     * This is a simplified block placement method that uses the material's
     * default block state. For more complex block data, use the
     * {@link #setType(int, int, int, XBlockData)} overload.
     * </p>
     *
     * @param x the X coordinate within the section (0-15)
     * @param y the Y coordinate within the section (section-relative)
     * @param z the Z coordinate within the section (0-15)
     * @param material the {@link Material} to place at the specified location
     */
    void setType(int x, int y, int z, @NotNull Material material);

    /**
     * Sets the block type and data at the specified coordinates within this section.
     * <p>
     * This method places a block with the specified block data at the given
     * coordinates within the section. The {@link XBlockData} provides
     * cross-version compatibility for block states, properties, and legacy
     * data values.
     * </p>
     * <p>
     * This method should be used when placing blocks that require specific
     * properties, orientations, or states that cannot be represented by
     * a simple material type.
     * </p>
     *
     * @param x the X coordinate within the section (0-15)
     * @param y the Y coordinate within the section (section-relative)
     * @param z the Z coordinate within the section (0-15)
     * @param blockData the {@link XBlockData} containing material and state information
     */
    void setType(int x, int y, int z, @NotNull XBlockData blockData);

    /**
     * Checks if this chunk section is empty.
     * <p>
     * An empty section contains only air blocks and has no meaningful
     * block data. Empty sections are often optimized by the Minecraft
     * engine to save memory and processing time.
     * </p>
     * <p>
     * This method is useful for determining whether a section needs
     * processing or can be skipped in bulk operations.
     * </p>
     *
     * @return {@code true} if the section contains only air blocks, {@code false} otherwise
     */
    boolean isEmpty();
}
