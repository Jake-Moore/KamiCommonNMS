package com.kamikazejam.kamicommon.actions;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Basic hover action showing an {@link ItemStack}.<br>
 * <br>
 * Use {@link Action#setHoverItem(ItemStack)} to add this hover action to an {@link Action}.<br>
 * - This class is not intended to be instantiated directly.
 */
@Getter
@SuppressWarnings({"unused"})
public class HoverItem extends Hover {
    private final @NotNull ItemStack itemStack;

    /**
     * @param itemStack The item to show when hovering
     */
    HoverItem(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}