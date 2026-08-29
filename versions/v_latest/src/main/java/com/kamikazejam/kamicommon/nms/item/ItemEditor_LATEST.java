package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * The 26.x twin of {@code ItemEditor_1_13_R1} in {@code versions/v1_13_R1}, and the only reason it exists is to be
 * compiled.
 * <p>
 * Nothing dispatches here. The ladder sends every server, 26.x included, to the v1_13_R1 copy, because
 * under this project's convention a class lives in the module named for the FIRST version it works
 * on. That is correct for dispatch and bad for early warning: it means the implementation is only
 * ever compiled against an old dev bundle, so an API this code uses could be removed in 26.x and the
 * build would not notice until a server did.
 * </p><p>
 * This copy closes that. It compiles against {@code highestPaperDep}, so bumping that version
 * compile-checks this capability against bleeding-edge Paper. If it stops compiling, that IS the
 * finding: fix it here and in the twin, and add a ladder branch if the two must now differ.
 * </p>
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
        Object damageableSource = item.getItemMeta();
        if (damageableSource instanceof Damageable) {
            Damageable damageable = (Damageable) damageableSource;
            damageable.setDamage(damage);
            //noinspection RedundantCast
            item.setItemMeta((ItemMeta) damageable);
            return item;
        }
        throw new IllegalArgumentException("ItemMeta is not Damageable");
    }

    @Override
    public int getDamage(@NotNull ItemStack item) {
        // 1.13+ method
        Object damageableSource = item.getItemMeta();
        if (damageableSource instanceof Damageable) {
            Damageable damageable = (Damageable) damageableSource;
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
