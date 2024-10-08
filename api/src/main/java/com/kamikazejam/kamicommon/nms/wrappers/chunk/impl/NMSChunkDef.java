package com.kamikazejam.kamicommon.nms.wrappers.chunk.impl;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.nms.abstraction.block.PlaceType;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunk;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@SuppressWarnings("unused")
public interface NMSChunkDef extends NMSChunk {

    @Override
    default void saveAndRefresh() {
        this.saveAndRefreshI();
    }

    // Create a default inside this internal interface, to not expose this method
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
