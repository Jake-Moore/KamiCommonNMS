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
     * Retrieves the internal internationalization name for an item.
     * <p>
     * This method returns the internal name that Minecraft uses for item
     * localization and translation.
     * </p>
     *
     * @param item the {@link ItemStack} to get the internal name for
     * @return the internal internationalization name used by Minecraft
     */
    @NotNull
    String getI18NItemName(@NotNull ItemStack item);
}
