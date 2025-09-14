package com.kamikazejam.kamicommon.nms.util;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_1_18_R1;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_LATEST;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Utility for interacting with {@link VersionedComponent} and various server apis.
 * <p>
 * This provider selects the appropriate {@link VersionedComponent} implementation
 * based on the current Minecraft version, handling the evolution of
 * the adventure api and its integration natively into paper.
 * </p>
 *
 * @see VersionedComponent
 */
@SuppressWarnings("unused")
public class VersionedComponentUtil {

    /**
     * Mirror method to {@link NmsAPI#getVersionedComponentSerializer()} for convenience.
     */
    public static @NotNull VersionedComponentSerializer serializer() {
        return NmsAPI.getVersionedComponentSerializer();
    }

    private static int f(String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }

    /**
     * Set the display name of an item stack using a {@link VersionedComponent}.<br>
     * <br>
     * On older versions the component is serialized to legacy codes, while on newer versions it uses the native adventure api.
     * @return the same {@link ItemMeta} instance (for chaining).
     */
    public static @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent component) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        int ver = NmsVersion.getFormattedNmsInteger();
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Select the correct wrapper which knows how to send this kind of component
        if (ver <= f("1.18.1")) {
            // uses shaded components
            return VersionedComponent_1_18_R1.setDisplayName(meta, component);
        }

        // 1.18.2+ has adventure bundled, so we can use the native apis
        return VersionedComponent_LATEST.setDisplayName(meta, component);
    }

    /**
     * Set the lore of an item stack using a list of {@link VersionedComponent}s.<br>
     * <br>
     * On older versions the components are serialized to legacy codes, while on newer versions it uses the native adventure api.
     * @param meta the item meta to modify
     * @param lore the lore lines as versioned components, or null to remove lore
     * @return the same {@link ItemMeta} instance (for chaining).
     */
    public static @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        int ver = NmsVersion.getFormattedNmsInteger();
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Select the correct wrapper which knows how to send this kind of component
        if (ver <= f("1.18.1")) {
            // uses shaded components
            return VersionedComponent_1_18_R1.setLore(meta, lore);
        }

        // 1.18.2+ has adventure bundled, so we can use the native apis
        return VersionedComponent_LATEST.setLore(meta, lore);
    }

    /**
     * Get the lore of an item stack as a list of {@link VersionedComponent}s.<br>
     * <br>
     * Returns null if the item has no lore.
     * @param meta the item meta to read from
     * @return the lore lines as versioned components, or null if no lore
     */
    public static @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        int ver = NmsVersion.getFormattedNmsInteger();
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Select the correct wrapper which knows how to send this kind of component
        if (ver <= f("1.18.1")) {
            // uses shaded components
            return VersionedComponent_1_18_R1.getLore(meta);
        }

        // 1.18.2+ has adventure bundled, so we can use the native apis
        return VersionedComponent_LATEST.getLore(meta);
    }

    /**
     * Get the display name of an item stack as a {@link VersionedComponent}.<br>
     * <br>
     * Returns null if the item has no custom display name.
     * @param meta the item meta to read from
     * @return the display name as a versioned component, or null if no custom name
     */
    public static @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        int ver = NmsVersion.getFormattedNmsInteger();
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Select the correct wrapper which knows how to send this kind of component
        if (ver <= f("1.18.1")) {
            // uses shaded components
            return VersionedComponent_1_18_R1.getDisplayName(meta);
        }

        // 1.18.2+ has adventure bundled, so we can use the native apis
        return VersionedComponent_LATEST.getDisplayName(meta);
    }

    /**
     * Add a single line to the lore of an item stack using a {@link VersionedComponent}.<br>
     * <br>
     * If the item has no existing lore, creates a new lore list with this component.
     * @param meta the item meta to modify
     * @param component the lore line to add
     * @return the same {@link ItemMeta} instance (for chaining).
     */
    public static @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent component) {
        Preconditions.checkNotNull(meta, "meta cannot be null");
        Preconditions.checkNotNull(component, "component cannot be null");
        int ver = NmsVersion.getFormattedNmsInteger();
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Select the correct wrapper which knows how to send this kind of component
        if (ver <= f("1.18.1")) {
            // uses shaded components
            return VersionedComponent_1_18_R1.addLoreLine(meta, component);
        }

        // 1.18.2+ has adventure bundled, so we can use the native apis
        return VersionedComponent_LATEST.addLoreLine(meta, component);
    }
}
