package com.kamikazejam.kamicommon.nms.text;

import org.jetbrains.annotations.ApiStatus;
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
 * The native Adventure implementation for 1.18.2 through 1.21.3.
 *
 * <p>These servers have had native Adventure with MiniMessage since 1.18.2 and were only using the
 * relocated copy because {@code VersionedComponent_1_21_4} needs {@code ItemMeta.customName()},
 * which Paper added in 1.21.4. That is avoidable: {@code ItemMeta.setDisplayName(String)} has existed
 * since 1.8, so this writes item names as legacy section-coded strings and uses native Adventure for
 * everything else.
 *
 * <p>Worth more than it looks. Since the relocated Adventure moved into a nested jar, every server
 * below 1.21.4 extracts roughly 1.5 MB to disk at enable and builds a child classloader to read it.
 * This tier removes that for ten versions, 1.18.2 through 1.21.3, and renders through the server's
 * own Adventure rather than a bundled 4.x copy.
 *
 * <p>The fidelity cost is real and bounded: legacy section codes cannot express hover or click on an
 * item name, which item names do not support anyway, and cannot express full RGB below 1.16. Every
 * server this tier serves is 1.18.2 or newer, so RGB is available through the section-code extension.
 */
@ApiStatus.Internal
public class VersionedComponent_1_18_R2 implements ModernVersionedComponent {
    private final @NotNull Component component;
    private VersionedComponent_1_18_R2(@NotNull Component component) {
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
    public static @NotNull VersionedComponent_1_18_R2 fromJson(@NotNull String json) {
        return new VersionedComponent_1_18_R2(net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(json));
    }

    @Internal
    public static @NotNull VersionedComponent_1_18_R2 fromPlainText(@NotNull String plainText) {
        Preconditions.checkNotNull(plainText, "plainText cannot be null");
        return new VersionedComponent_1_18_R2(PlainTextComponentSerializer.plainText().deserialize(plainText));
    }

    @Internal
    public static @NotNull VersionedComponent_1_18_R2 fromMiniMessage(@NotNull String miniMessage) {
        Preconditions.checkNotNull(miniMessage, "miniMessage cannot be null");
        return new VersionedComponent_1_18_R2(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    @Internal
    public static @NotNull VersionedComponent_1_18_R2 fromLegacyAmpersand(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return new VersionedComponent_1_18_R2(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
    }

    @Internal
    public static @NotNull VersionedComponent_1_18_R2 fromLegacySection(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return new VersionedComponent_1_18_R2(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

    // GsonComponentSerializer, not JSONComponentSerializer. The latter did not exist in the Adventure
    // Paper shipped with 1.18.2; it arrived later in 4.x. Both emit Minecraft's component JSON, so the
    // wire format is identical and a component crosses between this tier and v1_21_4 unchanged.
    @Override
    public @NotNull String serializeJson() {
        return net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(this.component);
    }

    @Override
    public @NotNull Component asNativeComponent() {
        return this.component;
    }

    @Override
    public @NotNull VersionedComponent append(@NotNull VersionedComponent other) {
        @NotNull Component otherComp;
        if (other instanceof VersionedComponent_1_18_R2 vcLatest) {
            otherComp = vcLatest.component;
        } else {
            String miniMessage = other.serializeMiniMessage();
            otherComp = MiniMessage.miniMessage().deserialize(miniMessage);
        }
        return new VersionedComponent_1_18_R2(this.component.append(otherComp));
    }

    @Override
    public @NotNull VersionedComponent click(@NotNull ClickAction action, @NotNull String value) {
        return new VersionedComponent_1_18_R2(this.component.clickEvent(toClickEvent(action, value)));
    }

    @Override
    public @NotNull VersionedComponent hover(@NotNull VersionedComponent tooltip) {
        return new VersionedComponent_1_18_R2(this.component.hoverEvent(HoverEvent.showText(nativeOf(tooltip))));
    }

    @Override
    public @NotNull VersionedComponent decorate(@NotNull TextDecoration decoration, boolean value) {
        return new VersionedComponent_1_18_R2(this.component.decoration(toDecoration(decoration), value));
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
        if (other instanceof VersionedComponent_1_18_R2 same) {
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
    @SuppressWarnings("deprecation")
    public static @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent name) {
        if (name == null) {
            meta.setDisplayName(null);
            return meta;
        }
        // Legacy section codes rather than customName(), which is the whole reason this tier exists.
        // setDisplayName(String) has existed since 1.8; customName() arrived in Paper 1.21.4.
        meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(nativeOf(name)));
        return meta;
    }
    public static @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        if (lore == null) {
            meta.lore(null);
            return meta;
        }
        List<Component> serializedLore = new ArrayList<>();
        for (VersionedComponent vc : lore) {
            if (vc instanceof VersionedComponent_1_18_R2 vcLatest) {
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
            vcList.add(new VersionedComponent_1_18_R2(line));
        }
        return vcList;
    }
    @SuppressWarnings("deprecation")
    public static @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        if (!meta.hasDisplayName()) {
            return null;
        }
        return new VersionedComponent_1_18_R2(
                LegacyComponentSerializer.legacySection().deserialize(meta.getDisplayName()));
    }
    public static @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent line) {
        List<Component> lore = (meta.hasLore() && meta.lore() != null) ? meta.lore() : Collections.emptyList();
        List<Component> newLore = new ArrayList<>(Objects.requireNonNull(lore));
        if (line instanceof VersionedComponent_1_18_R2 vcLatest) {
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
    public static @NotNull VersionedComponent_1_18_R2 fromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders) {
        return new VersionedComponent_1_18_R2(MiniMessage.miniMessage().deserialize(miniMessage, toResolver(placeholders)));
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
