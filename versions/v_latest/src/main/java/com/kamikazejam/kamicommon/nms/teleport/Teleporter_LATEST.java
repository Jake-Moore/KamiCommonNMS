package com.kamikazejam.kamicommon.nms.teleport;

import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

@SuppressWarnings({"DuplicatedCode"})
public class Teleporter_LATEST extends AbstractTeleporter {

    @Override
    public void teleportWithoutEvent(Player player, Location location) {
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        if (location.getWorld() == null) { return; }

        final ServerLevel toWorld = ((CraftWorld)location.getWorld()).getHandle();
        final ServerLevel fromWorld = ((CraftWorld)player.getWorld()).getHandle();
        final ServerPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if (toWorld == fromWorld) {
            // Starting in 1.21.10 this method no longer accepts a Location
            //  and requires each coordinate separately.
            entityPlayer.connection.teleport(
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch()
            );
        }
        else {
            // Starting in 1.21.10 minecraft changed some internal methods regarding teleportation between dimensions
            //  they also removed the location parameter from respawn method preventing direct cross-dimensional teleports
            // Our solution? Attempt to respawn the player in the target world (at the default spawn location)
            //  then immediately teleport them to the desired location in that world.

            // 1. Respawn the player in the target dimension (AT THE DEFAULT SPAWN LOCATION)
            MinecraftServer.getServer().getPlayerList().respawn(
                    entityPlayer, true, Entity.RemovalReason.DISCARDED,
                    PlayerRespawnEvent.RespawnReason.PLUGIN
            );
            // 2. Teleport the player to the desired location (which should work now that they are in the correct dimension)
            entityPlayer.connection.teleport(
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch()
            );
        }
    }
}
