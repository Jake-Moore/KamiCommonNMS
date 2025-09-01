package com.kamikazejam.kamicommon.nms.abstraction.item;

import org.bukkit.inventory.ItemStack;
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

    /**
     * @since 1.1.3
     * Checks if an item is unbreakable through its {@link ItemMeta}.
     * <p>
     * This method retrieves the unbreakable status from the provided
     * {@link ItemMeta}. It returns {@code true} if the item is unbreakable,
     * and {@code false} otherwise.
     * </p>
     * <p>
     * The implementation handles version-specific differences in how
     * the unbreakable flag is stored and accessed in item metadata.
     * </p>
     *
     * @param meta the {@link ItemMeta} to check
     * @return {@code true} if the item is unbreakable, {@code false} otherwise
     */
    public abstract boolean isUnbreakable(@NotNull ItemMeta meta);

    /**
     * @since 1.1.4
     * Sets the damage (or 'durability') of an item.
     * <p>
     * In pre-1.13 versions of Minecraft, item durability is set directly on the {@link ItemStack}<br>
     * In 1.13 and later versions, item durability is managed through the {@link ItemMeta}.
     * </p>
     *
     * @param item the {@link ItemStack} to modify
     * @param damage the damage value to set (0 for undamaged, higher values for more damage)
     * @throws IllegalArgumentException if the item's meta does not support damage (e.g., non-damageable items)
     */
    public abstract ItemStack setDamage(@NotNull ItemStack item, int damage);

    /**
     * @since 1.1.4
     * Gets the damage (or 'durability') of an item.
     * <p>
     * In pre-1.13 versions of Minecraft, item durability is retrieved directly from the {@link ItemStack}<br>
     * In 1.13 and later versions, item durability is managed through the {@link ItemMeta}.
     * </p>
     *
     * @param item the {@link ItemStack} to check
     * @return the current damage value (0 for undamaged, higher values for more damage)
     * @throws IllegalArgumentException if the item's meta does not support damage (e.g., non-damageable items)
     */
    public abstract int getDamage(@NotNull ItemStack item);
}
