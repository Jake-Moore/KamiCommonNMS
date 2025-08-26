package com.kamikazejam.kamicommon.nms.library.worldguard;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Version-specific API interface for WorldGuard integration across different Minecraft versions.
 * <p>
 * This interface provides a unified abstraction layer for WorldGuard operations that may
 * vary between different WorldGuard and Minecraft versions. It handles region protection
 * queries, PvP status checking, build permissions, and player location tracking within
 * protected regions.
 * </p>
 * <p>
 * All methods in this interface are designed to work consistently across WorldGuard
 * version changes, abstracting away the differences in WorldGuard's internal APIs
 * between major version updates.
 * </p>
 */
@SuppressWarnings("unused")
public interface WorldGuardApi {

    /**
     * Checks if PvP (Player vs Player combat) is allowed for the specified player.
     * <p>
     * This method checks the PvP status at the player's current location, taking
     * into account WorldGuard region flags, global world settings, and any other
     * PvP-related protections that may be in effect.
     * </p>
     *
     * @param player the {@link Player} to check PvP status for
     * @return {@code true} if PvP is allowed for the player at their current location,
     *         {@code false} if PvP is disabled by WorldGuard protections
     */
    boolean canPVP(@NotNull Player player);

    /**
     * Checks if PvP (Player vs Player combat) is allowed for the specified player at a specific location.
     * <p>
     * This method checks the PvP status at the specified location, taking into account
     * WorldGuard region flags, global world settings, and any other PvP-related
     * protections that may be in effect at that location.
     * </p>
     *
     * @param player the {@link Player} to check PvP status for
     * @param location the {@link Location} to check PvP status at
     * @return {@code true} if PvP is allowed for the player at the specified location,
     *         {@code false} if PvP is disabled by WorldGuard protections
     */
    boolean canPVP(@NotNull Player player, @NotNull Location location);

    /**
     * Checks if the specified player has build permissions at the given location.
     * <p>
     * This method verifies whether the player can place or break blocks at the
     * specified location, taking into account WorldGuard region memberships,
     * permissions, flags, and any other build-related protections.
     * </p>
     * <p>
     * This check includes both explicit build permissions and implicit permissions
     * through region membership, ownership, or global permissions.
     * </p>
     *
     * @param player the {@link Player} to check build permissions for
     * @param loc the {@link Location} to check build permissions at
     * @return {@code true} if the player can build at the specified location,
     *         {@code false} if building is prohibited by WorldGuard protections
     */
    boolean playerCanBuild(@NotNull Player player, @NotNull Location loc);

    /**
     * Checks if the specified chunk contains any WorldGuard regions.
     * <p>
     * This method determines whether any WorldGuard protected regions overlap
     * with the given chunk. This is useful for optimization purposes when
     * bulk operations need to know if WorldGuard protections may be relevant
     * in a particular area.
     * </p>
     *
     * @param chunk the {@link Chunk} to check for region presence
     * @return {@code true} if the chunk contains or overlaps with any WorldGuard regions,
     *         {@code false} if the chunk is completely unprotected
     */
    boolean hasRegionsInChunk(@NotNull Chunk chunk);

    /**
     * Retrieves all players currently located within the specified WorldGuard region.
     * <p>
     * This method returns a list of all online players whose current location
     * is within the boundaries of the named WorldGuard region. Players are
     * included regardless of their permissions or membership status in the region.
     * </p>
     * <p>
     * The region name should match exactly with a defined WorldGuard region.
     * If the region does not exist, an empty list is returned.
     * </p>
     *
     * @param regionName the name of the WorldGuard region to search
     * @return a {@link List} of {@link Player} instances currently within the region,
     *         or an empty list if no players are in the region or the region doesn't exist
     */
    @NotNull
    List<Player> getPlayersInRegion(@NotNull String regionName);

    /**
     * Retrieves the version string of the WorldGuard plugin currently in use.
     * <p>
     * This method returns the version information for the WorldGuard plugin,
     * which can be useful for debugging, compatibility checking, or feature
     * availability determination.
     * </p>
     *
     * @return the WorldGuard version string (e.g., "7.0.4", "6.2.1")
     */
    @NotNull
    String getVersion();

}
