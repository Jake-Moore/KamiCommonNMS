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
        // These are plain legacy-section operations on the Bukkit ItemMeta API, identical for
        // every version below 1.18.2, so there is one copy and it sits at Java 8, the lowest
        // floor any server in that range needs.
        if (ver < f("1.21.4")) { return NmsBundles.forModule("v1_16_R3"); }
        // 1.18.2+ has adventure bundled, so we can use the native apis. That is v1_18_R2, not
        // v_latest: 1.18.2 runs Java 17 and v_latest targets 21 for Paper 26.x.
        return NmsBundles.forModule("v1_21_4");
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
