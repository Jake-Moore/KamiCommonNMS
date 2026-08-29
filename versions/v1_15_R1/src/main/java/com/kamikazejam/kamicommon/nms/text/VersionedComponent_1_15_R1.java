package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.MiniMessage;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

/**
 * Confirmed For: 1_12_R1, 1_13_R1, 1_14_R1, 1_15_R1
 */
public class VersionedComponent_1_15_R1 implements VersionedComponent {
    private final @NotNull Component component;
    public VersionedComponent_1_15_R1(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public void sendTo(@NotNull CommandSender sender) {
        // Use direct spigot method
        sender.spigot().sendMessage(BungeeComponentSerializer.legacy().serialize(this.component));
    }

    @Override
    public @NotNull String serializeMiniMessage() {
        return MiniMessage.miniMessage().serialize(component);
    }

    @Override
    public @NotNull String serializeLegacyAmpersand() {
        return LegacyComponentSerializer.legacyAmpersand().serialize(this.component);
    }

    @Override
    public @NotNull String serializeLegacySection() {
        return LegacyComponentSerializer.legacySection().serialize(this.component);
    }

    @Override
    public @NotNull String serializePlainText() {
        return PlainTextComponentSerializer.plainText().serialize(this.component);
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder owner, int size) {
        // Needs to be serialized into legacy string that contains section symbols for title
        String title = LegacyComponentSerializer.legacySection().serialize(this.component);
        return Bukkit.createInventory(owner, size, title);
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder owner, @NotNull InventoryType type) {
        // Needs to be serialized into legacy string that contains section symbols for title
        String title = LegacyComponentSerializer.legacySection().serialize(this.component);
        return Bukkit.createInventory(owner, type, title);
    }

    @Override
    public @NotNull Component asInternalComponent() {
        return this.component;
    }

    @Override
    public @NotNull VersionedComponent append(@NotNull VersionedComponent other) {
        return new VersionedComponent_1_15_R1(this.component.append(other.asInternalComponent()));
    }

    /**
     * The legacy section-coded text, so that concatenating a component into a string produces the
     * message rather than an object identity.
     * <p>
     * Object.toString() cannot be given a default in {@link VersionedComponent}, so every
     * implementation carries this. Without it, anything doing {@code "prefix " + component} logged
     * {@code ...VersionedComponent_1_11_R1@3466377f}, which is what the console showed on every
     * server below 1.18.2.
     * </p>
     *
     * @return this component as legacy section-coded text
     */
    @Override
    public String toString() {
        return this.serializeLegacySection();
    }

    // ItemMeta operations, forked into this module rather than shared.
    //
    // The string work is identical in every module: display names and lore are legacy-section
    // strings on every version this library supports. What is NOT identical is the type the two
    // read methods construct, and that decides how the result serializes when it is later sent.
    // LegacyComponentSerializer.legacySection() decodes the section-x hex form, so a component read
    // back off an ItemMeta really can carry a hex colour, and VersionedComponent_1_15_R1's sendTo is the
    // one that knows whether this server can render it. Routing every version at one module put a
    // hex-capable component on servers that predate hex.
    //
    // Serves 1.12 through 1.15.2.

    @SuppressWarnings("deprecation")
    public static @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent name) {
        if (name == null) {
            meta.setDisplayName(null);
            return meta;
        }
        meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(name.asInternalComponent()));
        return meta;
    }

    @SuppressWarnings("deprecation")
    public static @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        if (lore == null) {
            meta.setLore(null);
            return meta;
        }
        List<String> serializedLore = lore.stream()
                .map(vc -> LegacyComponentSerializer.legacySection().serialize(vc.asInternalComponent()))
                .collect(Collectors.toList());
        meta.setLore(serializedLore);
        return meta;
    }

    @SuppressWarnings("deprecation")
    public static @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta) {
        if (!meta.hasLore()) {
            return null;
        }
        @Nullable List<String> lore = meta.getLore();
        if (lore == null) {
            return null;
        }
        return lore.stream()
                .map(line -> (VersionedComponent) new VersionedComponent_1_15_R1(LegacyComponentSerializer.legacySection().deserialize(line)))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    public static @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        if (!meta.hasDisplayName()) {
            return null;
        }
        String name = meta.getDisplayName();
        return new VersionedComponent_1_15_R1(LegacyComponentSerializer.legacySection().deserialize(name));
    }

    @SuppressWarnings("deprecation")
    public static @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent line) {
        List<String> lore = (meta.hasLore() && meta.getLore() != null) ? meta.getLore() : Collections.emptyList();
        List<String> newLore = new ArrayList<>(lore);
        newLore.add(LegacyComponentSerializer.legacySection().serialize(line.asInternalComponent()));
        meta.setLore(newLore);
        return meta;
    }

}
