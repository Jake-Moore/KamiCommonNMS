package com.kamikazejam.kamicommon.nms.mainhand;

import com.kamikazejam.kamicommon.nms.abstraction.mainhand.AbstractMainHand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Working for: 1.9.X, 1.21.8
 */
public class MainHand_LATEST extends AbstractMainHand {
    @Override
    public @Nullable ItemStack getItemInMainHand(@NotNull PlayerInventory inventory) {
        return inventory.getItemInMainHand();
    }

    @Override
    public boolean isOffHand(@NotNull PlayerInteractEntityEvent event) {
        return event.getHand() == org.bukkit.inventory.EquipmentSlot.OFF_HAND;
    }

    @Override
    public void setItemInMainHand(@NotNull Player player, @Nullable ItemStack itemStack) {
        player.getInventory().setItemInMainHand(itemStack);
    }

    @Override
    public @Nullable ItemStack getItemInOffHand(@NotNull PlayerInventory playerInventory) {
        return playerInventory.getItemInOffHand();
    }

    @Override
    public void setItemInOffHand(@NotNull Player player, @Nullable ItemStack item) throws UnsupportedOperationException {
        player.getInventory().setItemInOffHand(item);
    }
}
