package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Working for: 1.13, 1.21.8
 */
@SuppressWarnings("unused")
public class ItemEditor_LATEST extends AbstractItemEditor {
    @Override
    public ItemMeta setUnbreakable(@NotNull ItemMeta meta, boolean unbreakable) {
        // 1.11+ method
        meta.setUnbreakable(unbreakable);
        return meta;
    }

    @Override
    public boolean isUnbreakable(@NotNull ItemMeta meta) {
        // 1.11+ method
        return meta.isUnbreakable();
    }

    @Override
    public ItemStack setDamage(@NotNull ItemStack item, int damage) {
        // 1.13+ method
        if (item.getItemMeta() instanceof Damageable damageable) {
            damageable.setDamage(damage);
            item.setItemMeta((ItemMeta) damageable);
            return item;
        }
        throw new IllegalArgumentException("ItemMeta is not Damageable");
    }

    @Override
    public int getDamage(@NotNull ItemStack item) {
        // 1.13+ method
        if (item.getItemMeta() instanceof Damageable damageable) {
            return damageable.getDamage();
        }
        throw new IllegalArgumentException("ItemMeta is not Damageable");
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack item) {
        // 1.13+ method
        return item.getItemMeta() instanceof Damageable;
    }
}
