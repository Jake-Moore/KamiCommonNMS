package com.kamikazejam.kamicommon.nms.serializer;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_1_11_R1;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_1_15_R1;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_1_16_R3;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_1_18_R1;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_LATEST;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.MiniMessage;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific adventure component wrappers.
 * <p>
 * This provider selects the appropriate {@link VersionedComponent} implementation
 * based on the current Minecraft version, handling the evolution of
 * the adventure api and its integration natively into paper.
 * </p>
 *
 * @see VersionedComponent
 */
@SuppressWarnings("unused")
public class VersionedComponentSerializer {

    /**
     * Convert a MiniMessage string into a {@link VersionedComponent} for this version.<br>
     * <br>
     * Does not convert or support legacy codes (&amp; or &sect;). See {@link #fromLegacyAmpersand(String)} and {@link #fromLegacySection(String)} for those.
     */
    public @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage) {
        Preconditions.checkNotNull(miniMessage, "miniMessage cannot be null");
        int ver = NmsVersion.getFormattedNmsInteger();
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Select the correct wrapper which knows how to send this kind of component
        if (ver < f("1.12")) {
            // uses shaded MiniMessage - 1.8 to 1.11.X
            return new VersionedComponent_1_11_R1(MiniMessage.miniMessage().deserialize(miniMessage));
        } else if (ver < f("1.16")) {
            // uses shaded MiniMessage - 1.12 to 1.15.X
            return new VersionedComponent_1_15_R1(MiniMessage.miniMessage().deserialize(miniMessage));
        } else if (ver < f("1.17")) {
            // uses shaded MiniMessage - 1.16.X (added hex support)
            return new VersionedComponent_1_16_R3(MiniMessage.miniMessage().deserialize(miniMessage));
        } else if (ver <= f("1.18.1")) {
            // uses shaded MiniMessage - 1.17.X to 1.18.1 (has adventure, but not MiniMessage)
            return new VersionedComponent_1_18_R1(MiniMessage.miniMessage().deserialize(miniMessage));
        }

        // 1.18.2+ has adventure and MiniMessage bundled, so we can use the native MiniMessage
        return VersionedComponent_LATEST.fromMiniMessage(miniMessage);
    }

    /**
     * Convert a legacy ampersand (&amp;) string into a {@link VersionedComponent} for this version.<br>
     * <br>
     * Will ignore section (&sect;) codes. See {@link #fromLegacySection(String)} for that.
     */
    public @NotNull VersionedComponent fromLegacyAmpersand(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        int ver = NmsVersion.getFormattedNmsInteger();
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Select the correct wrapper which knows how to send this kind of component
        if (ver < f("1.12")) {
            // uses shaded MiniMessage - 1.8 to 1.11.X
            return new VersionedComponent_1_11_R1(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
        } else if (ver < f("1.16")) {
            // uses shaded MiniMessage - 1.12 to 1.15.X
            return new VersionedComponent_1_15_R1(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
        } else if (ver < f("1.17")) {
            // uses shaded MiniMessage - 1.16.X (added hex support)
            return new VersionedComponent_1_16_R3(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
        } else if (ver <= f("1.18.1")) {
            // uses shaded MiniMessage - 1.17.X to 1.18.1 (has adventure, but not MiniMessage)
            return new VersionedComponent_1_18_R1(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
        }

        // 1.18.2+ has adventure and MiniMessage bundled, so we can use the native MiniMessage
        return VersionedComponent_LATEST.fromLegacyAmpersand(legacy);
    }

    /**
     * Convert a legacy section (&sect;) string into a {@link VersionedComponent} for this version.<br>
     * <br>
     * Will ignore ampersand (&amp;) codes. See {@link #fromLegacyAmpersand(String)} for that.
     */
    public @NotNull VersionedComponent fromLegacySection(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        int ver = NmsVersion.getFormattedNmsInteger();
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Select the correct wrapper which knows how to send this kind of component
        if (ver < f("1.12")) {
            // uses shaded MiniMessage - 1.8 to 1.11.X
            return new VersionedComponent_1_11_R1(LegacyComponentSerializer.legacySection().deserialize(legacy));
        } else if (ver < f("1.16")) {
            // uses shaded MiniMessage - 1.12 to 1.15.X
            return new VersionedComponent_1_15_R1(LegacyComponentSerializer.legacySection().deserialize(legacy));
        } else if (ver < f("1.17")) {
            // uses shaded MiniMessage - 1.16.X (added hex support)
            return new VersionedComponent_1_16_R3(LegacyComponentSerializer.legacySection().deserialize(legacy));
        } else if (ver <= f("1.18.1")) {
            // uses shaded MiniMessage - 1.17.X to 1.18.1 (has adventure, but not MiniMessage)
            return new VersionedComponent_1_18_R1(LegacyComponentSerializer.legacySection().deserialize(legacy));
        }

        // 1.18.2+ has adventure and MiniMessage bundled, so we can use the native MiniMessage
        return VersionedComponent_LATEST.fromLegacySection(legacy);
    }

    /**
     * Serialize a {@link VersionedComponent} back into a MiniMessage string.
     */
    public @NotNull String serializeMiniMessage(@NotNull VersionedComponent component) {
        Preconditions.checkNotNull(component, "component cannot be null");
        return component.serializeMiniMessage();
    }

    /**
     * Deserialize a MiniMessage string into a {@link VersionedComponent}.
     */
    public @NotNull VersionedComponent deserializeMiniMessage(@NotNull String miniMessage) {
        return fromMiniMessage(miniMessage);
    }

    private int f(String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }
}
