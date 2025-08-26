package com.kamikazejam.kamicommon.nms.abstraction.enchantid;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class providing version-specific implementations for retrieving
 * namespaced identifiers of Minecraft enchantments.
 * <p>
 * This abstraction handles the differences in enchantment identification
 * across different Minecraft versions, particularly the transition from
 * numeric IDs to namespaced identifiers that occurred in 1.13+.
 * </p>
 */
public abstract class AbstractEnchantID {

    /**
     * Retrieves the namespaced identifier for the given enchantment.
     * <p>
     * This method returns the string representation of an enchantment
     * that can be used for serialization, configuration storage, or
     * cross-version compatibility. The format may vary depending on
     * the Minecraft version.
     * </p>
     *
     * @param enchantment the {@link Enchantment} to get the identifier for
     * @return the namespaced string identifier for the enchantment (i.e. "minecraft:sharpness")
     */
    public abstract @NotNull String getNamespaced(Enchantment enchantment);
}
