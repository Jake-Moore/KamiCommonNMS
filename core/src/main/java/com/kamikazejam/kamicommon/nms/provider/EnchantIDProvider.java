package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.enchantid.AbstractEnchantID;
import com.kamikazejam.kamicommon.nms.enchantid.EnchantID_LATEST;
import com.kamikazejam.kamicommon.nms.enchantid.EnchantID_1_8_R1;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific enchantment ID implementations.
 * <p>
 * This provider selects the appropriate {@link AbstractEnchantID} implementation
 * based on the current Minecraft version, handling the transition from legacy
 * enchantment IDs to namespaced identifiers that occurred in Minecraft 1.13.
 * </p>
 * <p>
 * The provider addresses the "flattening" update's impact on enchantments:
 * <ul>
 * <li><strong>Pre-1.13:</strong> Numeric enchantment IDs and legacy name formats</li>
 * <li><strong>1.13+:</strong> Namespaced enchantment identifiers (e.g., "minecraft:sharpness")</li>
 * </ul>
 * </p>
 * <p>
 * This enables consistent enchantment identification across versions while providing
 * access to modern namespaced identifiers for compatibility with newer systems
 * and external tools that expect standardized enchantment names.
 * </p>
 *
 * @see AbstractEnchantID
 */
public class EnchantIDProvider extends Provider<AbstractEnchantID> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the enchantment ID implementation based on the 1.13 flattening update.
     * Pre-1.13 versions use legacy numeric IDs, while 1.13+ versions use
     * namespaced identifiers for enchantments.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractEnchantID} implementation
     */
    @Override
    protected @NotNull AbstractEnchantID provide(int ver) {
        // up to 1.16.1 uses BaseComponent[] as the second parameter of HoverEvent
        if (ver < 1130) {
            return new EnchantID_1_8_R1();
        }else {
            return new EnchantID_LATEST();
        }
    }
}
