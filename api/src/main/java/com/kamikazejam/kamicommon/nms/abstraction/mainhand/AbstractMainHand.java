package com.kamikazejam.kamicommon.nms.abstraction.mainhand;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract class providing version-specific implementations for main hand and
 * off-hand item management across different Minecraft versions.
 * <p>
 * This abstraction handles the differences in dual-wielding mechanics that
 * were introduced in Minecraft 1.9. It provides consistent methods for
 * accessing and modifying items in both hands, with proper version compatibility
 * for servers that don't support off-hand functionality.
 * </p>
 * <p>
 * For versions prior to 1.9, off-hand methods may throw
 * {@link UnsupportedOperationException} or return {@code null} values
 * as appropriate, since dual-wielding was not available in those versions.
 * </p>
 */
@SuppressWarnings("unused")
public abstract class AbstractMainHand {

    /**
     * Retrieves the item in the player's main hand.
     * <p>
     * This convenience method extracts the item from the player's main hand
     * by accessing their inventory. It provides a direct way to get the main
     * hand item without manually accessing the inventory.
     * </p>
     *
     * @param player the {@link Player} to get the main hand item from
     * @return the {@link ItemStack} in the main hand, or {@code null} if empty
     */
    public final @Nullable ItemStack getItemInMainHand(@NotNull Player player) {
        return this.getItemInMainHand(player.getInventory());
    }

    /**
     * Retrieves the item in the main hand from the specified inventory.
     * <p>
     * This method provides version-specific access to the main hand slot
     * in the player's inventory. The implementation handles differences
     * in how main hand items are stored across Minecraft versions.
     * </p>
     *
     * @param inventory the {@link PlayerInventory} to get the main hand item from
     * @return the {@link ItemStack} in the main hand, or {@code null} if empty
     */
    public abstract @Nullable ItemStack getItemInMainHand(@NotNull PlayerInventory inventory);

    /**
     * Retrieves the item in the player's off-hand.
     * <p>
     * This convenience method extracts the item from the player's off-hand
     * by accessing their inventory. For versions prior to 1.9, this may
     * return {@code null} as off-hand functionality was not available.
     * </p>
     *
     * @param player the {@link Player} to get the off-hand item from
     * @return the {@link ItemStack} in the off-hand, or {@code null} if empty
     *         or not supported
     */
    public final @Nullable ItemStack getItemInOffHand(@NotNull Player player) {
        return this.getItemInOffHand(player.getInventory());
    }

    /**
     * Retrieves the item in the off-hand from the specified inventory.
     * <p>
     * This method provides version-specific access to the off-hand slot
     * in the player's inventory. For versions prior to 1.9, this method
     * may return {@code null} as dual-wielding was not supported.
     * </p>
     *
     * @param inventory the {@link PlayerInventory} to get the off-hand item from
     * @return the {@link ItemStack} in the off-hand, or {@code null} if empty
     *         or not supported
     */
    public abstract @Nullable ItemStack getItemInOffHand(@NotNull PlayerInventory inventory);

    /**
     * Determines if a player interaction event was performed with the off-hand.
     * <p>
     * This method checks whether the specified interaction event was triggered
     * using the player's off-hand rather than their main hand. For versions
     * prior to 1.9, this will typically return {@code false} as only main
     * hand interactions were possible.
     * </p>
     *
     * @param event the {@link PlayerInteractEntityEvent} to check
     * @return {@code true} if the interaction was performed with the off-hand,
     *         {@code false} if it was performed with the main hand or off-hand
     *         is not supported
     */
    public abstract boolean isOffHand(@NotNull PlayerInteractEntityEvent event);

    /**
     * Sets the item in the player's main hand.
     * <p>
     * This method modifies the item held in the player's main hand slot.
     * The implementation handles version-specific differences in how
     * main hand items are stored and accessed.
     * </p>
     *
     * @param player the {@link Player} to modify
     * @param item the {@link ItemStack} to place in the main hand,
     *             or {@code null} to clear the slot
     */
    public abstract void setItemInMainHand(@NotNull Player player, @Nullable ItemStack item);

    /**
     * Sets the item in the player's off-hand.
     * <p>
     * This method modifies the item held in the player's off-hand slot.
     * For versions prior to 1.9, this method will throw an
     * {@link UnsupportedOperationException} as dual-wielding was not available.
     * </p>
     *
     * @param player the {@link Player} to modify
     * @param item the {@link ItemStack} to place in the off-hand,
     *             or {@code null} to clear the slot
     * @throws UnsupportedOperationException if the server version does not
     *                                      support off-hand functionality
     */
    public abstract void setItemInOffHand(@NotNull Player player, @Nullable ItemStack item) throws UnsupportedOperationException;
}
