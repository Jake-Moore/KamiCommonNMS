package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemNbt;
import com.kamikazejam.kamicommon.nms.provider.ItemNbtProvider;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.key.Key;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.nbt.api.BinaryTagHolder;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds the Adventure {@code show_item} hover for the tiers backed by the relocated Adventure copy.
 *
 * <p>Package-private, and shared by all four shaded tiers: {@code VersionedComponent_1_11_R1},
 * {@code VersionedComponent_1_15_R1}, {@code VersionedComponent_1_16_R3} and {@code
 * VersionedComponent_1_17_R1}. They differ in how they serialize on the way out, not in how they
 * read an item, so this is one copy rather than four.
 *
 * <p>The NBT comes from {@link AbstractItemNbt}, which lives in {@code :api}. These classes
 * load in a child classloader whose parent holds {@code :api}, and delegation is parent-first, so
 * the interface, the provider and {@code NmsBundles} all resolve upward. Nothing here names a
 * third-party library, which it could not do: a reference from inside the nested jar is never
 * rewritten when a consumer relocates this library into their own uber jar.
 */
final class ShadedItemHover {

    /**
     * One provider, resolved once. {@code Provider} caches its capability after the first call, so
     * the version ladder and the reflective adapter load happen on the first hover and never again.
     */
    private static final ItemNbtProvider ITEM_NBT = new ItemNbtProvider();

    private ShadedItemHover() {}

    /**
     * The hover event for an item.
     *
     * <p>Adventure's item hover holds an id, a count and a {@code tag} compound, and reassembles the
     * wire form from those three on the way out. The tag must therefore be the item's {@code tag}
     * alone. Passing the whole item NBT nests it under a second {@code tag} key and produces a hover
     * that renders as the plain item with no name and no lore.
     *
     * @throws IllegalArgumentException if the item is air
     */
    static @NotNull HoverEvent<HoverEvent.ShowItem> of(@NotNull ItemStack item) {
        if (item.getType() == Material.AIR) {
            throw new IllegalArgumentException("hoverItem(ItemStack) was given air, which has no item to show");
        }
        AbstractItemNbt itemNbt = ITEM_NBT.get();
        String id = itemNbt.getItemId(item);
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("no item id for " + item.getType() + ", so it cannot be shown on hover");
        }
        @Nullable String tag = itemNbt.getNbtTag(item);
        @Nullable BinaryTagHolder nbt = (tag == null) ? null : BinaryTagHolder.binaryTagHolder(tag);
        return HoverEvent.showItem(Key.key(id), item.getAmount(), nbt);
    }
}
