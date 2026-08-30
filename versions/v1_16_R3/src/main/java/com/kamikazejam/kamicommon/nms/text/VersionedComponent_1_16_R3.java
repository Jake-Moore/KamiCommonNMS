package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import com.kamikazejam.kamicommon.nms.text.TextPlaceholder;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.event.ClickEvent;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.event.HoverEvent;
import com.kamikazejam.kamicommon.nms.text.ClickAction;
import com.kamikazejam.kamicommon.nms.text.TextDecoration;
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
import org.jetbrains.annotations.ApiStatus.Internal;
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

    @Override
    public @NotNull VersionedComponent click(@NotNull ClickAction action, @NotNull String value) {
        return new VersionedComponent_1_16_R3(this.component.clickEvent(toClickEvent(action, value)));
    }

    @Override
    public @NotNull VersionedComponent hover(@NotNull VersionedComponent tooltip) {
        return new VersionedComponent_1_16_R3(this.component.hoverEvent(HoverEvent.showText(tooltip.asInternalComponent())));
    }

    @Override
    public @NotNull VersionedComponent decorate(@NotNull TextDecoration decoration, boolean value) {
        return new VersionedComponent_1_16_R3(this.component.decoration(toDecoration(decoration), value));
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

    private static @NotNull com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.format.TextDecoration toDecoration(@NotNull TextDecoration decoration) {
        switch (decoration) {
            case BOLD: return com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.format.TextDecoration.BOLD;
            case ITALIC: return com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.format.TextDecoration.ITALIC;
            case UNDERLINED: return com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.format.TextDecoration.UNDERLINED;
            case STRIKETHROUGH: return com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;
            case OBFUSCATED: return com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.format.TextDecoration.OBFUSCATED;
        }
        throw new UnsupportedOperationException("Unhandled TextDecoration: " + decoration);
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

    /**
     * MiniMessage with tag replacements, converting each {@link TextPlaceholder} into the resolver
     * type this module's Adventure copy uses.
     * <p>
     * The conversion lives here rather than at the call site precisely so that no caller has to name
     * a {@code TagResolver}. That is the leak this API closes.
     * </p>
     */
    @Internal
    public static @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders) {
        return new VersionedComponent_1_16_R3(MiniMessage.miniMessage().deserialize(miniMessage, toResolver(placeholders)));
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
                    resolvers[i] = Placeholder.component(placeholder.getKey(), placeholder.getComponentValue().asInternalComponent());
                    break;
                default:
                    // Adding a Kind without handling it here would otherwise drop the replacement
                    // and render the raw tag to players.
                    throw new UnsupportedOperationException("Unhandled TextPlaceholder.Kind: " + placeholder.getKind());
            }
        }
        return TagResolver.resolver(resolvers);
    }


    // These return the INTERFACE, not this class, and that is load-bearing rather than style.
    // NmsBundleImpl calls them; if they returned the concrete type the verifier would need a subtype
    // check against VersionedComponent when the adapter is loaded, which resolves this class and
    // through it the shaded Adventure. Loading ANY capability from this module would then drag the
    // shaded copy onto servers that have Adventure natively. Measured on Paper 26.2, where
    // commandMapModifier dispatches here.
    @Internal
    public static @NotNull VersionedComponent fromPlainText(@NotNull String text) {
        return new VersionedComponent_1_16_R3(PlainTextComponentSerializer.plainText().deserialize(text));
    }

    @Internal
    public static @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage) {
        return new VersionedComponent_1_16_R3(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    @Internal
    public static @NotNull VersionedComponent fromLegacyAmpersand(@NotNull String legacy) {
        return new VersionedComponent_1_16_R3(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
    }

    @Internal
    public static @NotNull VersionedComponent fromLegacySection(@NotNull String legacy) {
        return new VersionedComponent_1_16_R3(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

}
