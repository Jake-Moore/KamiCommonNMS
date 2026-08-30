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

import java.util.List;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

/**
 * Confirmed For: 1_17_R1, 1_18_R1
 */
@SuppressWarnings("deprecation")
@ApiStatus.Internal
public class VersionedComponent_1_17_R1 implements VersionedComponent, ShadedBacked {

    /**
     * The world data version of 1.17.1, read from that server's own {@code version.json}.
     *
     * <p>This tier serves 1.17 through 1.18.1, data versions 2724 to 2865. Adventure groups its
     * JSON options into ranges, and the range opened by 1.16 at 2526 is not closed until 1.20.3 at
     * 3679, so every version this tier serves selects the same option set and one pin covers all of
     * them.
     */
    private static final int DATA_VERSION_1_17_1 = 2730;

    // BungeeComponentSerializer.get() pinned to the client generation this tier serves.
    //
    // get() serializes with GsonComponentSerializer.gson(), whose defaults track current Minecraft
    // and emit the flattened "hover_event" and "click_event" keys introduced in 1.21.5. The
    // bungee-chat shipped with 1.17 and 1.18 reads "hoverEvent" and "clickEvent", so it discarded
    // both events and the component arrived carrying only its text. hover() and click() were
    // therefore no-ops on every server this tier serves.
    //
    // No legacy hover serializer is supplied, unlike v1_16_R3. That one exists to write a show_item
    // hover, and hoverItem(ItemStack) throws on this tier, so nothing here can produce one.
    private static final BungeeComponentSerializer SERIALIZER = BungeeComponentSerializer.of(
            GsonComponentSerializer.builder()
                    .options(JSONOptions.byDataVersion().at(DATA_VERSION_1_17_1))
                    .build(),
            LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build());

    final @NotNull Component component;
    public VersionedComponent_1_17_R1(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public void sendTo(@NotNull CommandSender sender) {
        // Use direct spigot method (deprecated, but lacks MiniMessage in 1.17 to fix)
        //   Have to use this because paper apis don't allow converting MiniMessage strings yet
        //    and thus we cannot obtain the right Component instance to use the paper method.
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
        // Use direct spigot method (deprecated, but lacks MiniMessage in 1.17 to fix)
        //   Have to use this because paper apis don't allow converting MiniMessage strings yet
        //    and thus we cannot obtain the right Component instance to use the paper method.
        // Needs to be serialized into legacy string that contains section symbols for title
        String title = LegacyComponentSerializer.legacySection().serialize(this.component);
        return Bukkit.createInventory(owner, size, title);
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder owner, @NotNull InventoryType type) {
        // Use direct spigot method (deprecated, but lacks MiniMessage in 1.17 to fix)
        //   Have to use this because paper apis don't allow converting MiniMessage strings yet
        //    and thus we cannot obtain the right Component instance to use the paper method.
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
        return new VersionedComponent_1_17_R1(this.component.append(ShadedBacked.of(other)));
    }

    @Override
    public @NotNull VersionedComponent click(@NotNull ClickAction action, @NotNull String value) {
        return new VersionedComponent_1_17_R1(this.component.clickEvent(toClickEvent(action, value)));
    }

    @Override
    public @NotNull VersionedComponent hover(@NotNull VersionedComponent tooltip) {
        return new VersionedComponent_1_17_R1(this.component.hoverEvent(HoverEvent.showText(ShadedBacked.of(tooltip))));
    }

    /**
     * Always throws. 1.17 and 1.18.1 have no item NBT source in this library and no native
     * Adventure, so there is nothing to build an item hover from.
     * <p>
     * {@code AbstractItemTextPre_1_17} stops at 1.16.5, as its name says, and native Adventure
     * arrives in 1.18.2. Throwing here is the same choice {@link ClickAction#COPY_TO_CLIPBOARD}
     * makes below 1.16: a hover that quietly shows the wrong thing is worse than one that fails
     * while it is being written.
     * </p>
     */
    @Override
    public @NotNull VersionedComponent hoverItem(@NotNull ItemStack item) {
        throw new UnsupportedOperationException(
                "hoverItem(ItemStack) needs Minecraft 1.18.2 or newer, or 1.16.5 or older; this server"
                        + " runs " + Bukkit.getVersion() + " and dispatches to "
                        + VersionedComponent_1_17_R1.class.getSimpleName());
    }

    @Override
    public @NotNull VersionedComponent decorate(@NotNull TextDecoration decoration, boolean value) {
        return new VersionedComponent_1_17_R1(this.component.decoration(toDecoration(decoration), value));
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
    // back off an ItemMeta really can carry a hex colour, and VersionedComponent_1_17_R1's sendTo is the
    // one that knows whether this server can render it. Routing every version at one module put a
    // hex-capable component on servers that predate hex.
    //
    // Serves 1.17 through 1.21.3.

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
                .map(line -> (VersionedComponent) new VersionedComponent_1_17_R1(LegacyComponentSerializer.legacySection().deserialize(line)))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    public static @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        if (!meta.hasDisplayName()) {
            return null;
        }
        String name = meta.getDisplayName();
        return new VersionedComponent_1_17_R1(LegacyComponentSerializer.legacySection().deserialize(name));
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
        return new VersionedComponent_1_17_R1(MiniMessage.miniMessage().deserialize(miniMessage, toResolver(placeholders)));
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
        return new VersionedComponent_1_17_R1(PlainTextComponentSerializer.plainText().deserialize(text));
    }

    @Internal
    public static @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage) {
        return new VersionedComponent_1_17_R1(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    @Internal
    public static @NotNull VersionedComponent fromLegacyAmpersand(@NotNull String legacy) {
        return new VersionedComponent_1_17_R1(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
    }

    @Internal
    public static @NotNull VersionedComponent fromLegacySection(@NotNull String legacy) {
        return new VersionedComponent_1_17_R1(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

}
