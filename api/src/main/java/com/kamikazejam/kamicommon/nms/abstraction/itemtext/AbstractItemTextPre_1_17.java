package com.kamikazejam.kamicommon.nms.abstraction.itemtext;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

/**
 * Interface providing version-specific implementations for item text and tooltip
 * generation for Minecraft versions prior to 1.17.
 * <p>
 * This interface handles the extraction of item display information and tooltips
 * using the methods available in pre-1.17 Minecraft versions. It provides access
 * to both component-based and NBT-based tooltip representations that were used
 * before the major text component system changes in 1.17+.
 * </p>
 * <p>
 * The implementation focuses on compatibility with older text systems and
 * provides methods for accessing item information in formats that were
 * standard before the modern component system restructuring.
 * </p>
 */
@SuppressWarnings("unused")
public interface AbstractItemTextPre_1_17 {

    /**
     * Retrieves the text components that represent the item's display information.
     * <p>
     * This method extracts the {@link BaseComponent} array that represents
     * how the item would be displayed in chat, tooltips, or other text contexts.
     * The components include the item name, lore, enchantments, and other
     * displayable properties formatted according to pre-1.17 standards.
     * </p>
     * <p>
     * The returned components can be used for custom chat integration,
     * hover text generation, or other display purposes where the full
     * item representation is needed in component form.
     * </p>
     *
     * @param item the {@link ItemStack} to extract components from
     * @return an array of {@link BaseComponent} representing the item's display information
     */
    BaseComponent[] getComponents(ItemStack item);

    /**
     * Retrieves the NBT-based string representation of the item's tooltip.
     * <p>
     * This method extracts the raw NBT string that represents the item's
     * tooltip information as it would appear in-game. This includes all
     * item properties, enchantments, lore, and metadata formatted as a
     * string representation of the underlying NBT structure.
     * </p>
     * <p>
     * This method is useful for debugging, serialization, or when you need
     * direct access to the raw tooltip data without component parsing.
     * The returned string represents the complete tooltip structure in
     * NBT format.
     * </p>
     *
     * @param item the {@link ItemStack} to extract tooltip NBT from
     * @return the NBT string representation of the item's tooltip
     */
    String getNbtStringTooltip(ItemStack item);
}
