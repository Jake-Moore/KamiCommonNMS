package com.kamikazejam.kamicommon.nms.library.worldguard;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public interface WorldGuardApi {

    boolean canPVP(@NotNull Player player);

    boolean canPVP(@NotNull Player player, @NotNull Location location);

    boolean playerCanBuild(@NotNull Player player, @NotNull Location loc);

    boolean hasRegionsInChunk(@NotNull Chunk chunk);

    @NotNull
    List<Player> getPlayersInRegion(@NotNull String regionName);

    @NotNull
    String getVersion();

}
