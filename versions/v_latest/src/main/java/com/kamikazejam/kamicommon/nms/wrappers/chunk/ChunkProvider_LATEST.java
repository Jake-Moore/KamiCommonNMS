package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld_LATEST;
import net.minecraft.server.level.ServerChunkCache;
import org.jetbrains.annotations.NotNull;

public class ChunkProvider_LATEST implements NMSChunkProvider {
    private final @NotNull NMSWorld_LATEST world;
    private final @NotNull ServerChunkCache handle;
    public ChunkProvider_LATEST(@NotNull NMSWorld_LATEST world, @NotNull ServerChunkCache server) {
        this.world = world;
        this.handle = server;
    }

    @Override
    public @NotNull NMSWorld getNMSWorld() {
        return this.world;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.handle;
    }

    @Override
    public void saveChunk(@NotNull NMSChunk chunk) {
    }

    @Override
    public @NotNull NMSChunk wrap(org.bukkit.@NotNull Chunk chunk) {
        return new Chunk_LATEST(this, chunk);
    }
}
