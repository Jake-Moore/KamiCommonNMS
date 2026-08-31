package com.kamikazejam.kamicommon.nms.abstraction.item;

import org.bukkit.inventory.ItemStack;

/**
 * Reads the two pieces of item NBT an Adventure {@code show_item} hover is built from.
 * <p>
 * Adventure holds an item hover as an id, a count and a {@code tag} compound, and reassembles the
 * wire form from those three. Servers from 1.18.2 onward hand that over through their own
 * {@code ItemStack.asHoverEvent()}; below it, the values have to be read from NMS, which is what
 * this interface is for.
 * </p>
 * <p>
 * Implemented for every server version below 1.18.2. From 1.8 to 1.16.5 the implementation is the
 * one {@link com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17} already
 * carried, which is why that interface extends this one; 1.17 and 1.18.1 have their own.
 * </p>
 */
@SuppressWarnings("unused")
public interface AbstractItemNbt {

    /**
     * The item's namespaced id, as the server's own item registry writes it.
     *
     * @param item the {@link ItemStack} to read
     * @return the namespaced id, for example {@code minecraft:wool}, or an empty string for air
     */
    String getItemId(ItemStack item);

    /**
     * The item's {@code tag} compound as an SNBT string, or {@code null} when it has none.
     * <p>
     * This is the inner compound holding the display name, lore and enchantments. It is not the
     * whole item NBT, which also carries {@code id}, {@code Count} and, below 1.13, {@code Damage}.
     * Handing the whole item over instead nests it under a second {@code tag} key and produces a
     * hover that renders as the plain item with no name and no lore.
     * </p>
     *
     * @param item the {@link ItemStack} to read
     * @return the {@code tag} compound as SNBT, or {@code null} when the item carries no tag
     */
    String getNbtTag(ItemStack item);
}
