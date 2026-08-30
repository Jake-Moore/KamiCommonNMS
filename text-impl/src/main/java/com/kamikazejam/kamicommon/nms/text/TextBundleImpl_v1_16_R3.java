package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * {@code v1_16_R3}'s text implementation, reached through {@code TextBundles} from the child
 * classloader. Lives in the nested jar because everything it delegates to names the relocated
 * Adventure.
 */
public final class TextBundleImpl_v1_16_R3 implements TextBundle {

    @Override
    public @NotNull VersionedComponent fromPlainText(@NotNull String text) {
        return VersionedComponent_1_16_R3.fromPlainText(text);
    }

    @Override
    public @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage) {
        return VersionedComponent_1_16_R3.fromMiniMessage(miniMessage);
    }

    @Override
    public @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders) {
        return VersionedComponent_1_16_R3.fromMiniMessage(miniMessage, placeholders);
    }

    @Override
    public @NotNull VersionedComponent fromLegacyAmpersand(@NotNull String legacy) {
        return VersionedComponent_1_16_R3.fromLegacyAmpersand(legacy);
    }

    @Override
    public @NotNull VersionedComponent fromLegacySection(@NotNull String legacy) {
        return VersionedComponent_1_16_R3.fromLegacySection(legacy);
    }

    @Override
    public @NotNull VersionedComponent fromJson(@NotNull String json) {
        return new VersionedComponent_1_16_R3(JSONComponentSerializer.json().deserialize(json));
    }

    @Override
    public @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent component) {
        return VersionedComponent_1_16_R3.setDisplayName(meta, component);
    }

    @Override
    public @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        return VersionedComponent_1_16_R3.getDisplayName(meta);
    }

    @Override
    public @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        return VersionedComponent_1_16_R3.setLore(meta, lore);
    }

    @Override
    public @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta) {
        return VersionedComponent_1_16_R3.getLore(meta);
    }

    @Override
    public @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent component) {
        return VersionedComponent_1_16_R3.addLoreLine(meta, component);
    }

    @Override
    public @NotNull ComponentLoggerAdapter componentLoggerAdapter() {
        return new ComponentLoggerAdapter_1_16_R3();
    }
}
