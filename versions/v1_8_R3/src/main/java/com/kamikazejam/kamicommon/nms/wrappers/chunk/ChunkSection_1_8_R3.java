package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.util.data.XBlockData;
import com.kamikazejam.kamicommon.nms.util.data.XMaterialData;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class ChunkSection_1_8_R3 implements NMSChunkSection {
    private final @NotNull Chunk_1_8_R3 chunk;
    private final @NotNull ChunkSection section;
    public ChunkSection_1_8_R3(@NotNull Chunk_1_8_R3 chunk, @NotNull ChunkSection section) {
        this.chunk = chunk;
        this.section = section;
    }

    @Override
    public @NotNull NMSChunk getNMSChunk() {
        return this.chunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.section;
    }

    @Override
    public void setType(int x, int y, int z, @NotNull Material material) {
        this.section.setType(x, y, z, CraftMagicNumbers.getBlock(material).getBlockData());
    }

    @Override
    public void setType(int x, int y, int z, @NotNull XBlockData xBlockData) {
        // For pre-1.13 we use data values
        XMaterialData materialData = xBlockData.getMaterialData();

        byte data = materialData.getData();
        Material material = materialData.getMaterial().parseMaterial();
        assert material != null;

        IBlockData blockData = CraftMagicNumbers.getBlock(material).fromLegacyData(data);
        this.section.setType(x, y, z, blockData);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
