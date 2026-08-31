package com.kamikazejam.kamicommon.nms.abstraction.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Interface providing version-specific implementations for NMS item operations
 * that require direct access to Minecraft's internal item system.
 * <p>
 * This interface handles item-related operations that cannot be performed
 * through the standard Bukkit API and require version-specific NMS access.
 * It provides methods for retrieving internal item identifiers and properties
 * that are used by Minecraft's internationalization and display systems.
 * </p>
 */
@SuppressWarnings("unused")
public interface NmsItemMethods {

    /**
     * The item's translated name, as a player sees it in an inventory.
     * <p>
     * The translation comes from the language table the server itself ships, which is English. This
     * is the item's own name and not any custom display name on the stack, so a renamed diamond
     * sword still returns {@code Diamond Sword}. The key is taken from the stack rather than from
     * the item, so variants that name themselves, such as potions and spawn eggs, are named
     * correctly.
     * </p>
     *
     * @param item the {@link ItemStack} to name
     * @return the translated name, or the translation key itself when the table has no entry for it
     */
    @NotNull
    String getI18NItemName(@NotNull ItemStack item);
}
