package com.kamikazejam.kamicommon.nms.text;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Every text capability, for one server version, expressed without naming Adventure.
 *
 * <p>Implementations live in the nested {@code internal-libs/adventure.jar} and are loaded through a
 * child classloader, because they are the classes that touch the relocated Adventure. This interface
 * is the boundary: it lives on the plugin classloader, names only {@link VersionedComponent}, Bukkit
 * types and {@code String}, and is therefore safe for a module's {@code NmsBundleImpl} to reference.
 *
 * <p>Split out of {@code NmsBundleImpl} because an adapter is loaded whenever <i>any</i> capability
 * from its module is used. On 26.2 {@code commandMapModifier} dispatches to {@code v1_17_R1}, so an
 * adapter that referenced the text implementations directly dragged the relocated Adventure onto a
 * server that has Adventure natively and never touches the shaded copy.
 *
 * <p>{@code fromJson} and {@code toJson} are how the native tier crosses the boundary. A JSON string
 * carries a component between the server's own Adventure and the relocated one without either side
 * naming the other's types.
 */
public interface TextBundle {

    @NotNull VersionedComponent fromPlainText(@NotNull String text);

    @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage);

    @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders);

    @NotNull VersionedComponent fromLegacyAmpersand(@NotNull String legacy);

    @NotNull VersionedComponent fromLegacySection(@NotNull String legacy);

    /** JSON in, component out. The native tier's route across the boundary. */
    @NotNull VersionedComponent fromJson(@NotNull String json);

    @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent component);

    @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta);

    @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore);

    @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta);

    @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent component);

    /**
     * Default-throwing: only {@code v1_16_R3} serves a logger adapter built on the relocated
     * Adventure. From 1.18.2 upward the adapter uses the server's own Adventure and lives in its
     * version module, outside the nested jar entirely.
     */
    default @NotNull ComponentLoggerAdapter componentLoggerAdapter() {
        throw new UnsupportedOperationException(
                getClass().getName() + " provides no componentLoggerAdapter() implementation");
    }
}
