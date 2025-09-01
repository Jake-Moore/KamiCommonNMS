package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ItemEditor_1_11_R1 extends AbstractItemEditor {
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
        // Pre 1.13 method
        item.setDurability((short) damage);
        return item;
    }

    @Override
    public int getDamage(@NotNull ItemStack item) {
        // Pre 1.13 method
        return item.getDurability();
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack item) {
        // Pre 1.13 method
        return item.getType().getMaxDurability() > 0;
    }
}
