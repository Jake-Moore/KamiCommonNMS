package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import com.kamikazejam.kamicommon.util.Preconditions;
import java.util.Collections;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import com.kamikazejam.kamicommon.nms.text.TextPlaceholder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import com.kamikazejam.kamicommon.nms.text.ClickAction;
import com.kamikazejam.kamicommon.nms.text.TextDecoration;
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
 * Confirmed For: 1_18_R2 to 1.21.8, 1.21.9
 * <br>
 * 1_18_R2 was the first version of paper to ship with kyori adventure **MiniMessage** support.
 */
public class VersionedComponent_1_21_4 implements ModernVersionedComponent {
    private final @NotNull Component component;
    private VersionedComponent_1_21_4(@NotNull Component component) {
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
    public static @NotNull VersionedComponent_1_21_4 fromInternalComponent(@NotNull com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component component) {
        Preconditions.checkNotNull(component, "component cannot be null");
        // Use JSON to convert between shaded and native component types
        String json = JSONComponentSerializer.json().serialize(component);
        return new VersionedComponent_1_21_4(net.kyori.adventure.text.serializer.json.JSONComponentSerializer.json().deserialize(json));
    }

    @Internal
    public static @NotNull VersionedComponent_1_21_4 fromPlainText(@NotNull String plainText) {
        Preconditions.checkNotNull(plainText, "plainText cannot be null");
        return new VersionedComponent_1_21_4(PlainTextComponentSerializer.plainText().deserialize(plainText));
    }

    @Internal
    public static @NotNull VersionedComponent_1_21_4 fromMiniMessage(@NotNull String miniMessage) {
        Preconditions.checkNotNull(miniMessage, "miniMessage cannot be null");
        return new VersionedComponent_1_21_4(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    @Internal
    public static @NotNull VersionedComponent_1_21_4 fromLegacyAmpersand(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return new VersionedComponent_1_21_4(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
    }

    @Internal
    public static @NotNull VersionedComponent_1_21_4 fromLegacySection(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return new VersionedComponent_1_21_4(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

    @Override
    public @NotNull com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component asInternalComponent() {
        // We need to adapt the native component to the shaded component type. Use JSON as a bridge
        String json = net.kyori.adventure.text.serializer.json.JSONComponentSerializer.json().serialize(this.component);
        return JSONComponentSerializer.json().deserialize(json);
    }

    @Override
    public @NotNull Component asNativeComponent() {
        return this.component;
    }

    @Override
    public @NotNull VersionedComponent append(@NotNull VersionedComponent other) {
        @NotNull Component otherComp;
        if (other instanceof VersionedComponent_1_21_4 vcLatest) {
            otherComp = vcLatest.component;
        } else {
            String miniMessage = other.serializeMiniMessage();
            otherComp = MiniMessage.miniMessage().deserialize(miniMessage);
        }
        return new VersionedComponent_1_21_4(this.component.append(otherComp));
    }

    @Override
    public @NotNull VersionedComponent click(@NotNull ClickAction action, @NotNull String value) {
        return new VersionedComponent_1_21_4(this.component.clickEvent(toClickEvent(action, value)));
    }

    @Override
    public @NotNull VersionedComponent hover(@NotNull VersionedComponent tooltip) {
        return new VersionedComponent_1_21_4(this.component.hoverEvent(HoverEvent.showText(nativeOf(tooltip))));
    }

    @Override
    public @NotNull VersionedComponent decorate(@NotNull TextDecoration decoration, boolean value) {
        return new VersionedComponent_1_21_4(this.component.decoration(toDecoration(decoration), value));
    }

    /**
     * The native component behind another {@link VersionedComponent}.
     * <p>
     * Deliberately round-trips through MiniMessage rather than {@code asInternalComponent()} for a
     * foreign implementation. That method returns the <i>shaded</i> Adventure copy, and using it here
     * would put a shaded reference back on the path modern servers load, which is the coupling this
     * API exists to remove. Same reasoning as {@code append}.
     * </p>
     */
    private static @NotNull Component nativeOf(@NotNull VersionedComponent other) {
        if (other instanceof VersionedComponent_1_21_4 same) {
            return same.component;
        }
        return MiniMessage.miniMessage().deserialize(other.serializeMiniMessage());
    }

    private static @NotNull ClickEvent toClickEvent(@NotNull ClickAction action, @NotNull String value) {
        switch (action) {
            case RUN_COMMAND: return ClickEvent.runCommand(value);
            case SUGGEST_COMMAND: return ClickEvent.suggestCommand(value);
            case OPEN_URL: return ClickEvent.openUrl(value);
            case COPY_TO_CLIPBOARD: return ClickEvent.copyToClipboard(value);
        }
        // Unreachable today. Kept so that adding a constant to ClickAction fails here rather than
        // silently dropping the behaviour on this version.
        throw new UnsupportedOperationException("Unhandled ClickAction: " + action);
    }

    private static @NotNull net.kyori.adventure.text.format.TextDecoration toDecoration(@NotNull TextDecoration decoration) {
        switch (decoration) {
            case BOLD: return net.kyori.adventure.text.format.TextDecoration.BOLD;
            case ITALIC: return net.kyori.adventure.text.format.TextDecoration.ITALIC;
            case UNDERLINED: return net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
            case STRIKETHROUGH: return net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;
            case OBFUSCATED: return net.kyori.adventure.text.format.TextDecoration.OBFUSCATED;
        }
        throw new UnsupportedOperationException("Unhandled TextDecoration: " + decoration);
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
        if (name instanceof VersionedComponent_1_21_4 vcLatest) {
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
            if (vc instanceof VersionedComponent_1_21_4 vcLatest) {
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
            vcList.add(new VersionedComponent_1_21_4(line));
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
        return new VersionedComponent_1_21_4(name);
    }
    public static @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent line) {
        List<Component> lore = (meta.hasLore() && meta.lore() != null) ? meta.lore() : Collections.emptyList();
        List<Component> newLore = new ArrayList<>(Objects.requireNonNull(lore));
        if (line instanceof VersionedComponent_1_21_4 vcLatest) {
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

    /**
     * MiniMessage with tag replacements, converting each {@link TextPlaceholder} into the resolver
     * type this module's Adventure copy uses.
     * <p>
     * The conversion lives here rather than at the call site precisely so that no caller has to name
     * a {@code TagResolver}. That is the leak this API closes.
     * </p>
     */
    @Internal
    public static @NotNull VersionedComponent_1_21_4 fromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders) {
        return new VersionedComponent_1_21_4(MiniMessage.miniMessage().deserialize(miniMessage, toResolver(placeholders)));
    }

    private static @NotNull TagResolver toResolver(@NotNull TextPlaceholder[] placeholders) {
        TagResolver[] resolvers = new TagResolver[placeholders.length];
        for (int i = 0; i < placeholders.length; i++) {
            TextPlaceholder placeholder = placeholders[i];
            switch (placeholder.getKind()) {
                case LITERAL:
                    resolvers[i] = Placeholder.unparsed(placeholder.getKey(), placeholder.getStringValue());
                    break;
                case MINI_MESSAGE:
                    resolvers[i] = Placeholder.parsed(placeholder.getKey(), placeholder.getStringValue());
                    break;
                case COMPONENT:
                    resolvers[i] = Placeholder.component(placeholder.getKey(), nativeOf(placeholder.getComponentValue()));
                    break;
                default:
                    // Adding a Kind without handling it here would otherwise drop the replacement
                    // and render the raw tag to players.
                    throw new UnsupportedOperationException("Unhandled TextPlaceholder.Kind: " + placeholder.getKind());
            }
        }
        return TagResolver.resolver(resolvers);
    }

}
