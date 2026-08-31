package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemNbt;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemNbt_1_17_R1 implements AbstractItemNbt {
    @Override
    public String getItemId(ItemStack item) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        if (nmsStack == null) { return ""; }
        // The server's own item registry, read back out of the compound save() writes, so that this
        // needs no per-version registry accessor.
        return nmsStack.save(new CompoundTag()).getString("id");
    }

    @Override
    public String getNbtTag(ItemStack item) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        if (nmsStack == null) { return null; }
        CompoundTag tag = nmsStack.getTag();
        return (tag == null) ? null : tag.toString();
    }
}
