package com.kamikazejam.kamicommon.nms.abstraction.item;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class providing version-specific implementations for editing
 * {@link ItemMeta} properties that may vary across Minecraft versions.
 * <p>
 * This abstraction handles the differences in item metadata manipulation
 * between different Minecraft versions, particularly for features that
 * were added or changed in newer versions.
 * </p>
 */
@SuppressWarnings("unused")
public abstract class AbstractItemEditor {

    /**
     * Sets the unbreakable status of an item through its {@link ItemMeta}.
     * <p>
     * This method modifies the provided {@link ItemMeta} to set whether
     * the item should be unbreakable. Unbreakable items do not lose
     * durability when used, regardless of their material type.
     * </p>
     * <p>
     * The implementation handles version-specific differences in how
     * the unbreakable flag is stored and accessed in item metadata.
     * </p>
     *
     * @param meta the {@link ItemMeta} to modify
     * @param unbreakable {@code true} to make the item unbreakable,
     *                   {@code false} to make it breakable
     * @return the modified {@link ItemMeta} for method chaining
     */
    public abstract ItemMeta setUnbreakable(@NotNull ItemMeta meta, boolean unbreakable);
}
