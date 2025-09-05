package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

/**
 * Working for: 1.20.5, 1.20.6, 1.21, 1.21.3, 1.21.4<br>
 * <br>
 * Changes in 1.21.5+ broke this implementation, see the separate package for 1.21.5+.
 */
public class ChunkSection_LATEST implements NMSChunkSection_1_13 {
    // In 1.21.5, the setBlockState method, which used to take two boolean flags for 'flag' and 'doPlace',
    //   has been replaced with a single integer bitmask flag argument.
    // We wanted `false` and `false` which is now represented best by integer 512
    // - Bit 0 (1) - NOT set: Prevents neighbor updates after block removal
    // - Bit 6 (64) - NOT set: Avoids additional neighbor update triggers
    // - Bit 8 (256) - NOT set: Allows block entity "pre-removal" side effects (this drops items from containers)
    // - Bit 9 (512) - SET: Prevents onPlace from being called
    // Bitmask 512 provides:
    // - No neighbor updates
    // - No physical updates
    // - No onPlace side effects
    // - Allows entity pre-removal side effects
    // Most Critical Bits:
    // - Method checks (flags & 512) == 0 and if true, calls onPlace (physics updates)
    public static final int SET_BLOCK_STATE_FLAG = 512;

    private final @NotNull Chunk_LATEST nmsChunk;
    private final @NotNull ChunkAccess chunk;
    private final int yShift;
    public ChunkSection_LATEST(@NotNull Chunk_LATEST nmsChunk, @NotNull ChunkAccess chunk, int yShift) {
        this.nmsChunk = nmsChunk;
        this.chunk = chunk;
        this.yShift = yShift;
    }

    @Override
    public @NotNull NMSChunk getNMSChunk() {
        return this.nmsChunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public void setType(int x, int y, int z, @NotNull Material material) {
        y += (this.yShift << 4);
        if (this.chunk instanceof LevelChunk c) {
            c.setBlockState(new BlockPos(x, y, z), CraftMagicNumbers.getBlock(material).defaultBlockState(), SET_BLOCK_STATE_FLAG);
        }else {
            this.chunk.setBlockState(new BlockPos(x, y, z), CraftMagicNumbers.getBlock(material).defaultBlockState(), SET_BLOCK_STATE_FLAG);
        }
    }

    @Override
    public void setType(int x, int y, int z, @NotNull BlockData blockData) {
        y += (this.yShift << 4);
        BlockState state = ((CraftBlockData) blockData).getState();
        if (this.chunk instanceof LevelChunk c) {
            c.setBlockState(new BlockPos(x, y, z), state, SET_BLOCK_STATE_FLAG);
        }else {
            this.chunk.setBlockState(new BlockPos(x, y, z), state, SET_BLOCK_STATE_FLAG);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.chunk.getSection(this.chunk.getSectionIndexFromSectionY(yShift)).hasOnlyAir();
    }
}
