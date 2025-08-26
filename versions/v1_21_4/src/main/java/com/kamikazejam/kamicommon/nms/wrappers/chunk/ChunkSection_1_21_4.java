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
public class ChunkSection_1_21_4 implements NMSChunkSection_1_13 {
    private final @NotNull Chunk_1_21_4 nmsChunk;
    private final @NotNull ChunkAccess chunk;
    private final int yShift;
    public ChunkSection_1_21_4(@NotNull Chunk_1_21_4 nmsChunk, @NotNull ChunkAccess chunk, int yShift) {
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
            c.setBlockState(new BlockPos(x, y, z), CraftMagicNumbers.getBlock(material).defaultBlockState(), false, false);
        }else {
            this.chunk.setBlockState(new BlockPos(x, y, z), CraftMagicNumbers.getBlock(material).defaultBlockState(), false);
        }
    }

    @Override
    public void setType(int x, int y, int z, @NotNull BlockData blockData) {
        y += (this.yShift << 4);
        BlockState state = ((CraftBlockData) blockData).getState();
        if (this.chunk instanceof LevelChunk c) {
            c.setBlockState(new BlockPos(x, y, z), state, false, false);
        }else {
            this.chunk.setBlockState(new BlockPos(x, y, z), state, false);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.chunk.getSection(this.chunk.getSectionIndexFromSectionY(yShift)).hasOnlyAir();
    }
}
