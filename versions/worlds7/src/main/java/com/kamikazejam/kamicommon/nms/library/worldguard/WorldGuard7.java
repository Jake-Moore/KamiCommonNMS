package com.kamikazejam.kamicommon.nms.library.worldguard;

import com.kamikazejam.kamicommon.nms.wrappers.NMSWrapper;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class WorldGuard7 implements WorldGuardApi {
    private final @NotNull WorldGuardPlugin wg;
    private final @NotNull NMSWrapper<NMSWorld, World> worldWrapper;
    public WorldGuard7(@NotNull Plugin wg, @NotNull NMSWrapper<NMSWorld, World> worldWrapper) {
        this.wg = (WorldGuardPlugin) wg;
        this.worldWrapper = worldWrapper;
    }

    @Override
    public boolean canPVP(@NotNull Player player) {
        return this.canPVP(player, player.getLocation());
    }

    @Override
    public boolean canPVP(@NotNull Player player, @NotNull Location location) {
        return this.checkFlags(player, location, Flags.PVP);
    }

    @Override
    public boolean playerCanBuild(@NotNull Player player, @NotNull Location loc) {
        return this.checkFlags(player, loc, Flags.BUILD);
    }

    private boolean checkFlags(@NotNull Player player, @NotNull Location location, @NotNull StateFlag... flags) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        Location pLoc = player.getLocation();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());
        com.sk89q.worldedit.util.Location loc = new com.sk89q.worldedit.util.Location(world, pLoc.getBlockX(), pLoc.getBlockY(), pLoc.getBlockZ());

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.testState(loc, localPlayer, flags);
    }

    @Override
    public boolean hasRegionsInChunk(@NotNull Chunk chunk) {
        NMSWorld world = this.worldWrapper.get(chunk.getWorld());

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(chunk.getWorld()));
        if (regionManager == null) { return false; }

        int minChunkX = chunk.getX() << 4;
        int minChunkZ = chunk.getZ() << 4;
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        BlockVector3 minChunk = BlockVector3.at(minChunkX, world.getMinHeight(), minChunkZ);
        BlockVector3 maxChunk = BlockVector3.at(maxChunkX, world.getMaxHeight(), maxChunkZ);

        ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgfactionoverlapcheck", minChunk, maxChunk);
        List<ProtectedRegion> allRegions = new ArrayList<>(regionManager.getRegions().values());

        try {
            List<ProtectedRegion> overlaps = region.getIntersectingRegions(allRegions);
            if (overlaps != null && !overlaps.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    @NotNull
    public List<Player> getPlayersInRegion(@NotNull String regionName) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        // Must search each world since there is no api to get a region name from all worlds
        for (World world : Bukkit.getWorlds()) {
            RegionManager manager = container.get(BukkitAdapter.adapt(world));
            ProtectedRegion region = manager.getRegion(regionName);
            if (region == null) { continue; }

            // We found the region, now we should check how many players are in it
            List<Player> players = new ArrayList<>();

            // No Native WorldGuard 7 API, we must check players in the world manually
            for (Player online : world.getPlayers()) {
                Location location = online.getLocation();
                if (region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                    players.add(online);
                }
            }
            return players;
        }

        return new ArrayList<>();
    }

    @Override
    public @NotNull String getVersion() {
        return wg.getDescription().getVersion();
    }
}
