package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtil1_13;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.jetbrains.annotations.NotNull;

import static com.kamikazejam.kamicommon.nms.wrappers.chunk.ChunkSection_LATEST.SET_BLOCK_STATE_FLAG;

/**
 * Working for: 1.21.5, 1.21.8, 1.21.9
 */
public class BlockUtil_LATEST extends IBlockUtil1_13<BlockState> {
    @Override
    public BlockState getIBlockData(@NotNull BlockData blockData) {
        return ((CraftBlockData) blockData).getState();
    }

    // physics = false, light = false
    @Override
    public void setNMS(@NotNull Block b, @NotNull BlockState ibd) {
        ServerLevel w = ((CraftWorld) b.getWorld()).getHandle();
        LevelChunk chunk = w.getChunk(b.getX() >> 4, b.getZ() >> 4);
        BlockPos bp = new BlockPos(b.getX(), b.getY(), b.getZ());

        BlockState old = chunk.getBlockStateIfLoaded(bp); // should be fine (and faster) to use this method
        try {
            // see ChunkSection_LATEST for details on the 512 bitmask flag
            chunk.setBlockState(bp, ibd, SET_BLOCK_STATE_FLAG);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        //noinspection ConstantValue
        w.sendBlockUpdated(bp, (old == null) ? ibd : old, ibd, 3);
    }
}
