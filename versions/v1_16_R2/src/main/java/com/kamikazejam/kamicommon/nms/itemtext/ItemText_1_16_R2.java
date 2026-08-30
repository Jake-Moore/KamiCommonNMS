package com.kamikazejam.kamicommon.nms.itemtext;

import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R2.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemText_1_16_R2 implements AbstractItemTextPre_1_17 {
    @Override
    public BaseComponent[] getComponents(ItemStack item) {
        net.minecraft.server.v1_16_R2.ItemStack v1_16_R2Stack = CraftItemStack.asNMSCopy(item);
        if (v1_16_R2Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_16_R2Stack.save(new NBTTagCompound()).toString()) };
    }

    @Override
    public String getNbtStringTooltip(ItemStack item) {
        net.minecraft.server.v1_16_R2.ItemStack v1_16_R2Stack = CraftItemStack.asNMSCopy(item);
        if (v1_16_R2Stack == null) { return ""; }
        return v1_16_R2Stack.save(new NBTTagCompound()).toString();
    }

    @Override
    public String getItemId(ItemStack item) {
        net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        if (nmsStack == null) { return ""; }
        // The server's own item registry, read back out of the compound save() writes, so that this
        // needs no per-version registry accessor. save() writes id as a namespaced string on every
        // version this interface covers.
        return nmsStack.save(new NBTTagCompound()).getString("id");
    }

    @Override
    public String getNbtTag(ItemStack item) {
        net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        if (nmsStack == null) { return null; }
        NBTTagCompound tag = nmsStack.getTag();
        return (tag == null) ? null : tag.toString();
    }
}
