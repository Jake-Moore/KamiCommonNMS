package com.kamikazejam.kamicommon.nms.text;

import org.jetbrains.annotations.ApiStatus;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import com.kamikazejam.kamicommon.nms.text.TextPlaceholder;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.event.ClickEvent;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.event.HoverEvent;
import com.kamikazejam.kamicommon.nms.text.ClickAction;
import com.kamikazejam.kamicommon.nms.text.TextDecoration;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.MiniMessage;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.gson.legacyimpl.NBTLegacyHoverEventSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.json.JSONOptions;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus.Internal;
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
@ApiStatus.Internal
public class VersionedComponent_1_15_R1 implements VersionedComponent, ShadedBacked {
    // BungeeComponentSerializer.legacy() with two additions, an explicit legacy hover serializer and
    // a click event key mode.
    //
    // legacy() is GsonComponentSerializer.builder().downsampleColors().emitLegacyHoverEvent(), and
    // emitLegacyHoverEvent on its own cannot write an item hover. Adventure has no default
    // LegacyHoverEventSerializer, so a show_item is emitted as an id and a count with no tag and no
    // pre-1.16 "value" field at all. The bungee-chat bundled with these versions reads only "value"
    // and throws NullPointerException when it is absent, so sending an item hover failed outright.
    // NBTLegacyHoverEventSerializer is what reassembles {id:...,Count:Nb,tag:{...}} into that field.
    //
    // emitLegacyHoverEvent has no click counterpart, so EMIT_CLICK_EVENT_TYPE kept Adventure's
    // default of the flattened "click_event" key introduced in 1.21.5. The bungee-chat of these
    // versions does not read that name and discarded the event, so click() produced a component
    // carrying only its text. BOTH emits the older "clickEvent" name alongside the newer one rather
    // than in place of it.
    //
    // Output is otherwise unchanged. Measured against the bungee-chat shipped in Paper 1.8.8 and
    // 1.12.2 for plain, coloured, hex-downsampled, show_text and show_item components: byte-identical
    // to legacy(), with the click event the only difference.
    private static final BungeeComponentSerializer SERIALIZER = BungeeComponentSerializer.of(
            GsonComponentSerializer.builder()
                    .downsampleColors()
                    .emitLegacyHoverEvent()
                    .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                    .editOptions(b -> b.value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.BOTH))
                    .build(),
            LegacyComponentSerializer.legacySection());

    private final @NotNull Component component;
    public VersionedComponent_1_15_R1(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public void sendTo(@NotNull CommandSender sender) {
        // Use direct spigot method
        sender.spigot().sendMessage(SERIALIZER.serialize(this.component));
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
    public @NotNull String serializeJson() {
        return JSONComponentSerializer.json().serialize(this.component);
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
    public @NotNull Component shadedComponent() {
        return this.component;
    }

    @Override
    public @NotNull VersionedComponent append(@NotNull VersionedComponent other) {
        return new VersionedComponent_1_15_R1(this.component.append(ShadedBacked.of(other)));
    }

    @Override
    public @NotNull VersionedComponent click(@NotNull ClickAction action, @NotNull String value) {
        return new VersionedComponent_1_15_R1(this.component.clickEvent(toClickEvent(action, value)));
    }

    @Override
    public @NotNull VersionedComponent hover(@NotNull VersionedComponent tooltip) {
        return new VersionedComponent_1_15_R1(this.component.hoverEvent(HoverEvent.showText(ShadedBacked.of(tooltip))));
    }

    @Override
    public @NotNull VersionedComponent hoverItem(@NotNull ItemStack item) {
        return new VersionedComponent_1_15_R1(this.component.hoverEvent(ShadedItemHover.of(item)));
    }

    @Override
    public @NotNull VersionedComponent decorate(@NotNull TextDecoration decoration, boolean value) {
        return new VersionedComponent_1_15_R1(this.component.decoration(toDecoration(decoration), value));
    }

    private static @NotNull ClickEvent toClickEvent(@NotNull ClickAction action, @NotNull String value) {
        switch (action) {
            case RUN_COMMAND: return ClickEvent.runCommand(value);
            case SUGGEST_COMMAND: return ClickEvent.suggestCommand(value);
            case OPEN_URL: return ClickEvent.openUrl(value);
            case COPY_TO_CLIPBOARD: throw new UnsupportedOperationException(
                    "COPY_TO_CLIPBOARD needs Minecraft 1.16 or newer; this server dispatches to "
                            + VersionedComponent_1_15_R1.class.getSimpleName());
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
        meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(ShadedBacked.of(name)));
        return meta;
    }

    @SuppressWarnings("deprecation")
    public static @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        if (lore == null) {
            meta.setLore(null);
            return meta;
        }
        List<String> serializedLore = lore.stream()
                .map(vc -> LegacyComponentSerializer.legacySection().serialize(ShadedBacked.of(vc)))
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
        newLore.add(LegacyComponentSerializer.legacySection().serialize(ShadedBacked.of(line)));
        meta.setLore(newLore);
        return meta;
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
        return new VersionedComponent_1_15_R1(MiniMessage.miniMessage().deserialize(miniMessage, toResolver(placeholders)));
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
                    resolvers[i] = Placeholder.component(placeholder.getKey(), ShadedBacked.of(placeholder.getComponentValue()));
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
        return new VersionedComponent_1_15_R1(PlainTextComponentSerializer.plainText().deserialize(text));
    }

    @Internal
    public static @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage) {
        return new VersionedComponent_1_15_R1(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    @Internal
    public static @NotNull VersionedComponent fromLegacyAmpersand(@NotNull String legacy) {
        return new VersionedComponent_1_15_R1(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
    }

    @Internal
    public static @NotNull VersionedComponent fromLegacySection(@NotNull String legacy) {
        return new VersionedComponent_1_15_R1(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

}
