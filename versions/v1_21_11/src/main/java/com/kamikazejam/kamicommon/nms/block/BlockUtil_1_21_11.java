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

import static com.kamikazejam.kamicommon.nms.wrappers.chunk.ChunkSection_1_21_11.SET_BLOCK_STATE_FLAG;

/**
 * Serves 1.21.5 through 1.21.11. The ladder in :core routes those versions here.
 *
 * The pre-26 copy of this implementation, compiled against the 1.21.11 dev bundle.
 *
 * <p>Its twin in {@code v_latest} is compiled against Paper 26.x and targets Java 25, because
 * 26.x requires Java 25. This one exists so that servers from 1.20.6 through 1.21.11, which run
 * on Java 21, still have an implementation they can load. The source is the same; only the dev
 * bundle and the JVM target differ, which is the same fork this library makes at every NMS
 * boundary, applied to a JVM boundary.</p>
 *
 * <p>Do not delete the {@code v_latest} copy in favour of this one. That module holds one of
 * every provider so that bumping the newest Paper dev bundle compile-checks them all, which is
 * how a 26.x API change gets noticed before it reaches a server.</p>
 *
 * <p>Keep the two in step. If you change one, change the other.</p>
 */
public class BlockUtil_1_21_11 extends IBlockUtil1_13<BlockState> {
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
            // see ChunkSection_1_21_11 for details on the 512 bitmask flag
            chunk.setBlockState(bp, ibd, SET_BLOCK_STATE_FLAG);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        //noinspection ConstantValue
        w.sendBlockUpdated(bp, (old == null) ? ibd : old, ibd, 3);
    }
}
