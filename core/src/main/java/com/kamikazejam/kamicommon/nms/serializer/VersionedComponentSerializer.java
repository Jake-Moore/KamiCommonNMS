package com.kamikazejam.kamicommon.nms.serializer;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundles;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class VersionedComponentSerializer {

    /**
     * Picks the module whose {@link VersionedComponent} implementation matches this server.
     * <p>
     * The thresholds are unchanged; they were simply repeated in all five factories below, which is
     * five places to keep in step. The modules up to 1.18.1 wrap the <i>shaded</i> adventure, while
     * {@code v_latest} delegates to the server's own — that difference is the reason 1.18.2 is a
     * boundary at all, so both paths are kept.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the module to construct through
     */
    private static @NotNull NmsBundle componentBundle(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }
        // uses shaded serializers - 1.8 to 1.11.X
        if (ver < f("1.12")) { return NmsBundles.forModule("v1_11_R1"); }
        // uses shaded serializers - 1.12 to 1.15.X
        if (ver < f("1.16")) { return NmsBundles.forModule("v1_15_R1"); }
        // uses shaded serializers - 1.16.X (added hex support)
        if (ver < f("1.17")) { return NmsBundles.forModule("v1_16_R3"); }
        // uses shaded serializers - 1.17.X to 1.18.1 (has adventure, but not MiniMessage)
        if (ver <= f("1.18.1")) { return NmsBundles.forModule("v1_18_R1"); }
        // 1.18.2+ has adventure and MiniMessage bundled, so we can use the native apis
        return NmsBundles.forModule("v_latest");
    }

    public @NotNull VersionedComponent fromInternalComponent(@NotNull com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component component) {
        Preconditions.checkNotNull(component, "component cannot be null");
        return componentBundle(NmsVersion.getFormattedNmsInteger()).componentFrom(component);
    }

    public @NotNull VersionedComponent fromPlainText(@NotNull String text) {
        Preconditions.checkNotNull(text, "text cannot be null");
        return componentBundle(NmsVersion.getFormattedNmsInteger()).componentFromPlainText(text);
    }

    public @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage) {
        Preconditions.checkNotNull(miniMessage, "miniMessage cannot be null");
        return componentBundle(NmsVersion.getFormattedNmsInteger()).componentFromMiniMessage(miniMessage);
    }

    public @NotNull VersionedComponent fromLegacyAmpersand(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return componentBundle(NmsVersion.getFormattedNmsInteger()).componentFromLegacyAmpersand(legacy);
    }

    public @NotNull VersionedComponent fromLegacySection(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return componentBundle(NmsVersion.getFormattedNmsInteger()).componentFromLegacySection(legacy);
    }

    public @NotNull String serializeMiniMessage(@NotNull VersionedComponent component) {
        Preconditions.checkNotNull(component, "component cannot be null");
        return component.serializeMiniMessage();
    }

    public @NotNull VersionedComponent deserializeMiniMessage(@NotNull String miniMessage) {
        return fromMiniMessage(miniMessage);
    }

    private static int f(String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }
}
