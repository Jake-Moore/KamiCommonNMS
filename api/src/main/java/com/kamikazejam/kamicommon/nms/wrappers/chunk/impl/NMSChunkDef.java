package com.kamikazejam.kamicommon.nms.wrappers.chunk.impl;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.nms.abstraction.block.PlaceType;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunk;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Default implementation interface for NMS chunk operations.
 * <p>
 * This interface provides default implementations for common chunk operations
 * that can be shared across different Minecraft versions. It extends the
 * base {@link NMSChunk} interface and provides concrete implementations
 * for complex operations like chunk saving and refreshing.
 * </p>
 * <p>
 * The implementation focuses on handling the nuances of chunk dirty state
 * management across different Minecraft versions, particularly addressing
 * the lazy saving behavior introduced in newer versions where chunks
 * may not be saved automatically after modifications.
 * </p>
 * <p>
 * This interface is typically implemented by version-specific chunk wrapper
 * classes that want to inherit the standard save and refresh behavior
 * while providing their own version-specific implementations for other
 * chunk operations.
 * </p>
 */
@SuppressWarnings("unused")
public interface NMSChunkDef extends NMSChunk {

    /**
     * {@inheritDoc}
     * <p>
     * This implementation delegates to the internal {@link #saveAndRefreshI()}
     * method to provide a complete save and refresh cycle for the chunk.
     * </p>
     */
    @Override
    default void saveAndRefresh() {
        this.saveAndRefreshI();
    }

    /**
     * Internal implementation of the save and refresh operation.
     * <p>
     * This method provides a robust implementation for chunk saving and
     * refreshing that works across multiple Minecraft versions. It addresses
     * the lazy saving behavior of newer versions by explicitly marking the
     * chunk as dirty through a temporary block modification.
     * </p>
     * <p>
     * The implementation strategy:
     * <ol>
     * <li>Temporarily modifies a block at the world's minimum height to trigger dirty state</li>
     * <li>Immediately reverts the block to its original state</li>
     * <li>Refreshes the chunk for all connected clients</li>
     * <li>Forces chunk unloading with save to ensure persistence</li>
     * </ol>
     * </p>
     * <p>
     * <strong>Note:</strong> This method is marked as internal and should not
     * be called directly by external code. Use {@link #saveAndRefresh()} instead.
     * </p>
     */
    @SuppressWarnings("deprecation")
    default void saveAndRefreshI() {
        // Higher versions are lazy about saving chunks, relying on dirty flags
        // We can set one block in the chunk in order to flag it as dirty, ensuring it saves
        // We select a block at the minimum y, since it's likely to be BEDROCK
        // Technically if there is a block data type here it will get overwritten, but this is unlikely
        NMSWorld nmsWorld = this.getNMSChunkProvider().getNMSWorld();
        Chunk chunk = this.getBukkitChunk();
        int minY = nmsWorld.getMinHeight();

        // Update, then revert the block (sufficient to trigger a 'dirty' state for this chunk)
        Block block = chunk.getBlock(0, minY, 0);
        Material prev = block.getType();
        nmsWorld.getBlockUtil().setBlockSuperFast(block, XMaterial.AIR, PlaceType.NMS);
        nmsWorld.getBlockUtil().setBlockSuperFast(block, XMaterial.matchXMaterial(prev), PlaceType.NMS);

        // Refresh the chunk
        World world = chunk.getWorld();
        world.refreshChunk(this.getX(), this.getZ());

        // Use the unload method with save
        world.unloadChunk(this.getX(), this.getZ(), true);
    }
}
