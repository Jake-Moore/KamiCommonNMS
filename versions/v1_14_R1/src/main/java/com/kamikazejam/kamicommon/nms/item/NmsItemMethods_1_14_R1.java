package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import net.minecraft.server.v1_14_R1.LocaleLanguage;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class NmsItemMethods_1_14_R1 implements NmsItemMethods {
    @Override
    public @NotNull String getI18NItemName(@NotNull ItemStack itemStack) {
        net.minecraft.server.v1_14_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        // The stack's own translation key, which for items such as potions and spawn eggs differs
        // from the item's. LocaleLanguage returns the key unchanged when it carries no translation.
        return LocaleLanguage.a().a(nmsCopy.j());
    }
}
