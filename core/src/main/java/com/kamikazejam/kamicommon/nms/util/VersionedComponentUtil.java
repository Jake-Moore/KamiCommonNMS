package com.kamikazejam.kamicommon.nms.util;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundles;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class VersionedComponentUtil {

    public static @NotNull VersionedComponentSerializer serializer() {
        return NmsAPI.getVersionedComponentSerializer();
    }

    /**
     * Picks the module that knows how to write components onto an {@link ItemMeta} here.
     * <p>
     * Same two-way split every method below used to repeat: up to 1.18.1 the shaded adventure is
     * used, and from 1.18.2 the server's own.
     * </p>
     *
     * @return the module to call through
     */
    private static @NotNull NmsBundle metaBundle() {
        int ver = NmsVersion.getFormattedNmsInteger();
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }
        // The SAME ladder the component serializer uses, deliberately.
        //
        // The ItemMeta string work is version independent, so this used to collapse everything
        // below 1.21.4 onto one module. But getDisplayName and getLore CONSTRUCT a
        // VersionedComponent, and its concrete type decides how the result serializes when it is
        // later sent: v1_16_R3 and above use BungeeComponentSerializer.get(), which emits hex, and
        // the modules below it use .legacy(), which does not. LegacyComponentSerializer
        // .legacySection() does decode the section-x hex form, so a name read off an ItemMeta on a
        // 1.12 server really can carry a hex colour, and collapsing the ladder handed that server a
        // hex-capable component. Splitting the same way keeps the type matched to the server.
        return VersionedComponentSerializer.bundleFor(ver);
    }

    public static @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent component) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        return metaBundle().setDisplayName(meta, component);
    }

    public static @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        return metaBundle().setLore(meta, lore);
    }

    public static @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        return metaBundle().getLore(meta);
    }

    public static @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        return metaBundle().getDisplayName(meta);
    }

    public static @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent component) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        Preconditions.checkNotNull(component, "component cannot be null");
        return metaBundle().addLoreLine(meta, component);
    }

    private static int f(String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }
}
