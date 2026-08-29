package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import com.kamikazejam.kamicommon.util.Preconditions;
import java.util.Collections;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The 26.x twin of {@code VersionedComponent_1_21_4} in {@code versions/v1_21_4}, and the only reason
 * it exists is to be compiled.
 * <p>
 * Nothing dispatches here. This is the highest value canary of the set, because it is the only
 * implementation that uses the server's OWN Adventure rather than the shaded copy, plus
 * {@code ItemMeta.customName()}, which is exactly the surface Paper moves. Compiling it against
 * {@code highestPaperDep} means bumping that version proves those calls still exist.
 * </p><p>
 * Two deliberate differences from the twin, neither of which touches a method body. It implements
 * {@code VersionedComponent} rather than {@code ModernVersionedComponent}, because that interface
 * lives in {@code versions/v1_18_R2} and a second copy of it in this package would collide with the
 * first in the shaded jar; and {@code asNativeComponent()} therefore carries no {@code @Override}.
 * Everything that actually calls a Paper API is identical, which is the part being checked.
 * </p>
 */
public class VersionedComponent_LATEST implements VersionedComponent {
    private final @NotNull Component component;
    private VersionedComponent_LATEST(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public void sendTo(@NotNull CommandSender sender) {
        // Send component directly using adventure method
        sender.sendMessage(this.component);
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
        return Bukkit.createInventory(owner, size, this.component);
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder owner, @NotNull InventoryType type) {
        return Bukkit.createInventory(owner, type, this.component);
    }

    @Internal
    public static @NotNull VersionedComponent_LATEST fromInternalComponent(@NotNull com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component component) {
        Preconditions.checkNotNull(component, "component cannot be null");
        // Use JSON to convert between shaded and native component types
        String json = JSONComponentSerializer.json().serialize(component);
        return new VersionedComponent_LATEST(net.kyori.adventure.text.serializer.json.JSONComponentSerializer.json().deserialize(json));
    }

    @Internal
    public static @NotNull VersionedComponent_LATEST fromPlainText(@NotNull String plainText) {
        Preconditions.checkNotNull(plainText, "plainText cannot be null");
        return new VersionedComponent_LATEST(PlainTextComponentSerializer.plainText().deserialize(plainText));
    }

    @Internal
    public static @NotNull VersionedComponent_LATEST fromMiniMessage(@NotNull String miniMessage) {
        Preconditions.checkNotNull(miniMessage, "miniMessage cannot be null");
        return new VersionedComponent_LATEST(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    @Internal
    public static @NotNull VersionedComponent_LATEST fromLegacyAmpersand(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return new VersionedComponent_LATEST(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
    }

    @Internal
    public static @NotNull VersionedComponent_LATEST fromLegacySection(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return new VersionedComponent_LATEST(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

    @Override
    public @NotNull com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component asInternalComponent() {
        // We need to adapt the native component to the shaded component type. Use JSON as a bridge
        String json = net.kyori.adventure.text.serializer.json.JSONComponentSerializer.json().serialize(this.component);
        return JSONComponentSerializer.json().deserialize(json);
    }

    // Declared by ModernVersionedComponent, which this canary cannot implement. See the class
    // javadoc: the method body is what is being compile-checked, not the interface wiring.
    public @NotNull Component asNativeComponent() {
        return this.component;
    }

    @Override
    public @NotNull VersionedComponent append(@NotNull VersionedComponent other) {
        @NotNull Component otherComp;
        if (other instanceof VersionedComponent_LATEST vcLatest) {
            otherComp = vcLatest.component;
        } else {
            String miniMessage = other.serializeMiniMessage();
            otherComp = MiniMessage.miniMessage().deserialize(miniMessage);
        }
        return new VersionedComponent_LATEST(this.component.append(otherComp));
    }

    // ------------------------------------------------------------ //
    //                        STATIC METHODS                        //
    // ------------------------------------------------------------ //
    public static @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent name) {
        if (name == null) {
            meta.displayName(null);
            return meta;
        }

        @NotNull Component nameComponent;
        if (name instanceof VersionedComponent_LATEST vcLatest) {
            nameComponent = vcLatest.component;
        } else {
            String miniMessage = name.serializeMiniMessage();
            nameComponent = MiniMessage.miniMessage().deserialize(miniMessage);
        }
        meta.customName(nameComponent);
        return meta;
    }
    public static @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        if (lore == null) {
            meta.lore(null);
            return meta;
        }
        List<Component> serializedLore = new ArrayList<>();
        for (VersionedComponent vc : lore) {
            if (vc instanceof VersionedComponent_LATEST vcLatest) {
                serializedLore.add(vcLatest.component);
            } else {
                String miniMessage = vc.serializeMiniMessage();
                Component comp = MiniMessage.miniMessage().deserialize(miniMessage);
                serializedLore.add(comp);
            }
        }
        meta.lore(serializedLore);
        return meta;
    }
    public static @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta) {
        if (!meta.hasLore()) {
            return null;
        }
        @Nullable List<Component> lore = meta.lore();
        if (lore == null) {
            return null;
        }
        List<VersionedComponent> vcList = new ArrayList<>();
        for (Component line : lore) {
            vcList.add(new VersionedComponent_LATEST(line));
        }
        return vcList;
    }
    public static @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        if (!meta.hasCustomName()) {
            return null;
        }
        Component name = meta.customName();
        if (name == null) {
            return null;
        }
        return new VersionedComponent_LATEST(name);
    }
    public static @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent line) {
        List<Component> lore = (meta.hasLore() && meta.lore() != null) ? meta.lore() : Collections.emptyList();
        List<Component> newLore = new ArrayList<>(Objects.requireNonNull(lore));
        if (line instanceof VersionedComponent_LATEST vcLatest) {
            newLore.add(vcLatest.component);
        } else {
            String miniMessage = line.serializeMiniMessage();
            Component comp = MiniMessage.miniMessage().deserialize(miniMessage);
            newLore.add(comp);
        }
        meta.lore(newLore);
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
