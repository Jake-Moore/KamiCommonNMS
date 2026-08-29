package com.kamikazejam.kamicommon.nms.mainhand;

import com.kamikazejam.kamicommon.nms.abstraction.mainhand.AbstractMainHand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The 26.x twin of {@code MainHand_1_9_R1} in {@code versions/v1_9_R1}, and the only reason it exists is to be
 * compiled.
 * <p>
 * Nothing dispatches here. The ladder sends every server, 26.x included, to the v1_9_R1 copy, because
 * under this project's convention a class lives in the module named for the FIRST version it works
 * on. That is correct for dispatch and bad for early warning: it means the implementation is only
 * ever compiled against an old dev bundle, so an API this code uses could be removed in 26.x and the
 * build would not notice until a server did.
 * </p><p>
 * This copy closes that. It compiles against {@code highestPaperDep}, so bumping that version
 * compile-checks this capability against bleeding-edge Paper. If it stops compiling, that IS the
 * finding: fix it here and in the twin, and add a ladder branch if the two must now differ.
 * </p>
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
