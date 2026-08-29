package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.MiniMessage;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Confirmed For: 1_16_R1, 1_16_R2, 1_16_R3
 * <br>
 * Uses BungeeComponentSerializer.get() since 1.16 added hex color support
 */
public class VersionedComponent_1_16_R3 implements VersionedComponent {
    private final @NotNull Component component;
    public VersionedComponent_1_16_R3(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public void sendTo(@NotNull CommandSender sender) {
        // Use direct spigot method
        sender.spigot().sendMessage(BungeeComponentSerializer.get().serialize(this.component));
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
        return new VersionedComponent_1_16_R3(this.component.append(other.asInternalComponent())) {};
    }

    // ------------------------------------------------------------------------------------------ //
    //  ItemMeta operations for servers below 1.17
    // ------------------------------------------------------------------------------------------ //
    // These are a copy of the ones in VersionedComponent_1_18_R1, and the duplication is the point.
    // They are pure legacy-section serialization over the Bukkit ItemMeta API, so the code is the
    // same. What differs is that v1_18_R1 targets Java 17, because 1.17 and 1.18 required it. Routing a 1.8.8
    // server there to reach identical code would load a Java 17 class on a Java 8 JVM. Forking the
    // block at the floor boundary is the same move this library already makes at every NMS
    // boundary, and it keeps every dispatch a module lookup.

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
                .map(line -> (VersionedComponent) new VersionedComponent_1_16_R3(LegacyComponentSerializer.legacySection().deserialize(line)))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    public static @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        if (!meta.hasDisplayName()) {
            return null;
        }
        String name = meta.getDisplayName();
        return new VersionedComponent_1_16_R3(LegacyComponentSerializer.legacySection().deserialize(name));
    }

    @SuppressWarnings("deprecation")
    public static @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent line) {
        List<String> lore = (meta.hasLore() && meta.getLore() != null) ? meta.getLore() : Collections.emptyList();
        List<String> newLore = new ArrayList<>(lore);
        newLore.add(LegacyComponentSerializer.legacySection().serialize(line.asInternalComponent()));
        meta.setLore(newLore);
        return meta;
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
}
