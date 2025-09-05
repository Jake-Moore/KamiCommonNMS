package com.kamikazejam.kamicommon.nms.enchantid;

import com.kamikazejam.kamicommon.nms.abstraction.enchantid.AbstractEnchantID;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

/**
 * Working for: 1.13, 1.21.8
 */
public class EnchantID_LATEST extends AbstractEnchantID {
    @Override
    public @NotNull String getNamespaced(Enchantment enchantment) {
        return enchantment.getKey().getKey();
    }
}
