package com.kamikazejam.kamicommon.nms.library.worldguard;

import com.kamikazejam.kamicommon.nms.wrappers.NMSWrapper;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class WorldGuard6 implements WorldGuardApi {
    private final @NotNull WorldGuardPlugin wg;
    private final @NotNull NMSWrapper<NMSWorld, World> worldWrapper;
    public WorldGuard6(@NotNull Plugin wg, @NotNull NMSWrapper<NMSWorld, World> worldWrapper) {
        this.wg = (WorldGuardPlugin) wg;
        this.worldWrapper = worldWrapper;
    }

    @Override
    public boolean canPVP(@NotNull Player player) {
        return this.canPVP(player, player.getLocation());
    }

    @Override
    public boolean canPVP(@NotNull Player player, @NotNull Location location) {
        World world = location.getWorld();
        Vector pt = toVector(location);

        RegionManager regionManager = wg.getRegionManager(world);
        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
        return set.allows(DefaultFlag.PVP);
    }

    // Check if player can build at location by worldguards rules.
    // Returns:
    //	True: Player can build in the region.
    //	False: Player can not build in the region.
    @Override
    public boolean playerCanBuild(@NotNull Player player, @NotNull Location loc) {
        return wg.canBuild(player, loc);
    }

    @Override
    public boolean hasRegionsInChunk(@NotNull Chunk chunk) {
        NMSWorld world = this.worldWrapper.get(chunk.getWorld());

        int minChunkX = chunk.getX() << 4;
        int minChunkZ = chunk.getZ() << 4;
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        BlockVector minChunk = new BlockVector(minChunkX, world.getMinHeight(), minChunkZ);
        BlockVector maxChunk = new BlockVector(maxChunkX, world.getMaxHeight(), maxChunkZ);

        RegionManager regionManager = wg.getRegionManager(chunk.getWorld());
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgfactionoverlapcheck", minChunk, maxChunk);
        Map<String, ProtectedRegion> allRegions = regionManager.getRegions();
        Collection<ProtectedRegion> allRegionsList = new ArrayList<>(allRegions.values());
        List<ProtectedRegion> overlaps;
        boolean foundregions = false;

        try {
            overlaps = region.getIntersectingRegions(allRegionsList);
            foundregions = overlaps != null && !overlaps.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foundregions;
    }

    @Override
    @NotNull
    public List<Player> getPlayersInRegion(@NotNull String regionName) {
        // Must search each world since there is no api to get a region name from all worlds
        for (World world : Bukkit.getWorlds()) {
            RegionManager manager = wg.getRegionManager(world);
            ProtectedRegion region = manager.getRegion(regionName);
            if (region == null) { continue; }

            // We found the region, now we should check how many players are in it
            List<Player> players = new ArrayList<>();

            // No Native WorldGuard 6 API, we must check players in the world manually
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
