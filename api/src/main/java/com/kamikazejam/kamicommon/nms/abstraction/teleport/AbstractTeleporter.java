package com.kamikazejam.kamicommon.nms.abstraction.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Abstract class providing version-specific implementations for player teleportation
 * without triggering Bukkit events.
 * <p>
 * This abstraction allows for direct teleportation of players using NMS methods,
 * bypassing the standard Bukkit teleportation system which fires events and
 * performs additional checks. This can be useful for internal teleportation
 * operations where events should not be triggered.
 * </p>
 * <p>
 * <strong>Warning:</strong> Using this class bypasses Bukkit's teleportation
 * events and safety checks. Ensure that any necessary validation is performed
 * manually when using these methods.
 * </p>
 */
public abstract class AbstractTeleporter {

    /**
     * Teleports a player to the specified location without triggering Bukkit events.
     * <p>
     * This method performs a direct teleportation using NMS methods, completely
     * bypassing Bukkit's event system. No {@link org.bukkit.event.player.PlayerTeleportEvent}
     * will be fired, and no plugins will be notified of the teleportation.
     * </p>
     * <p>
     * This is useful for internal operations where you need to move a player
     * without other plugins interfering or being notified of the movement.
     * </p>
     *
     * @param player the {@link Player} to teleport
     * @param location the target {@link Location} to teleport the player to
     */
    public abstract void teleportWithoutEvent(Player player, Location location);
}
