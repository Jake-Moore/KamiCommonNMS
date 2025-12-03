package com.kamikazejam.kamicommon.nms.teleport;

import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Confirmed Working for versions: [1.21.10]
 */
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
            //  they also reworked the PlayerList#respawn method making it unsuitable for a cross-dimension teleport.
            // This solution may not be complete or future-proof but it works for now.
            crossDimensionSilent(player, location);
        }
    }

    /**
     * Yay! 1.21.10 made us do this manually!<br>
     * <br>
     * This method is probably not complete or future-proof, but it works for now.
     */
    @SuppressWarnings("resource")
    private void crossDimensionSilent(org.bukkit.entity.Player player, Location targetLoc) {
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        ServerLevel targetLevel = ((CraftWorld) targetLoc.getWorld()).getHandle();
        ServerLevel oldLevel = sp.level();

        // 1. Safety Checks (Essential for Vanilla Parity)
        sp.stopRiding(); // CRITICAL: Prevent vehicle state corruption
        sp.resetFallDistance(); // Prevent fall damage carrying over

        // 2. Preload Chunk (Moonrise-Safe)
        targetLevel.getChunk(targetLoc.getBlockX() >> 4, targetLoc.getBlockZ() >> 4);

        // 3. Remove Entity from Old World
        oldLevel.removePlayerImmediately(sp, Entity.RemovalReason.CHANGED_DIMENSION);

        // 4. Revive the Player Instance
        sp.unsetRemoved();

        // 5. Update Internal Player State
        sp.setServerLevel(targetLevel);
        sp.gameMode.setLevel(targetLevel);
        sp.setPos(targetLoc.getX(), targetLoc.getY(), targetLoc.getZ());
        sp.setYHeadRot(targetLoc.getYaw());
        sp.setYRot(targetLoc.getYaw());
        sp.setXRot(targetLoc.getPitch());

        // 6. Send Respawn Packet
        CommonPlayerSpawnInfo spawnInfo = sp.createCommonSpawnInfo(targetLevel);
        sp.connection.send(new ClientboundRespawnPacket(spawnInfo, (byte) 3));

        // 7. Teleport the Player in the New World
        sp.connection.teleport(targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), targetLoc.getYaw(), targetLoc.getPitch());

        // 8. Add Entity to New World
        targetLevel.addDuringTeleport(sp);

        // 9. Send Level Info
        ((CraftServer) Bukkit.getServer()).getHandle().sendLevelInfo(sp, targetLevel);

        // 10. Resync Client Data

        // a) Player List & Visibility
        sp.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(Collections.singletonList(sp)));

        // b) Entity Metadata (Skin Layers)
        List<SynchedEntityData.DataValue<?>> entityData = sp.getEntityData().getNonDefaultValues();
        if (entityData != null) {
            sp.connection.send(new ClientboundSetEntityDataPacket(sp.getId(), entityData));
        }

        // c) World Difficulty
        sp.connection.send(new ClientboundChangeDifficultyPacket(targetLevel.getDifficulty(), targetLevel.getLevelData().isDifficultyLocked()));

        // d) View Distance (Visual Fix) - NEW
        sp.connection.send(new ClientboundSetChunkCacheRadiusPacket(targetLevel.getServer().getPlayerList().getViewDistance()));
        sp.connection.send(new ClientboundSetSimulationDistancePacket(targetLevel.getServer().getPlayerList().getSimulationDistance()));

        // e) Abilities
        sp.connection.send(new ClientboundPlayerAbilitiesPacket(sp.getAbilities()));

        // f) Experience
        sp.connection.send(new ClientboundSetExperiencePacket(sp.experienceProgress, sp.totalExperience, sp.experienceLevel));

        // g) Health & Food
        sp.connection.send(new ClientboundSetHealthPacket(sp.getHealth(), sp.getFoodData().getFoodLevel(), sp.getFoodData().getSaturationLevel()));

        // h) Attributes
        sp.connection.send(new ClientboundUpdateAttributesPacket(sp.getId(), sp.getAttributes().getSyncableAttributes()));

        // i) Held Item Slot
        // Note: sp.getInventory().selected is the raw field. Use getSelectedSlot() if your mappings wrapper provides it.
        sp.connection.send(new ClientboundSetHeldSlotPacket(sp.getInventory().getSelectedSlot()));

        // j) Potion Effects
        for (MobEffectInstance effect : sp.getActiveEffects()) {
            sp.connection.send(new ClientboundUpdateMobEffectPacket(sp.getId(), effect, false));
        }

        // k) Inventory
        sp.inventoryMenu.sendAllDataToRemote();
    }
}
