package com.kamikazejam.kamicommon.nms.serializer;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundles;
import com.kamikazejam.kamicommon.nms.text.TextPlaceholder;
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
     * every module from 1.18.2 up delegates to the server's own. That difference is the reason
     * 1.18.2 is a boundary at all, so both paths are kept.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the module to construct through
     */
    /**
     * The one ladder that decides which module builds a {@link VersionedComponent} for this server.
     * <p>
     * Public because {@code VersionedComponentUtil} must use the same one. Its ItemMeta read methods
     * construct components too, and the concrete type decides whether the result can carry a hex
     * colour when it is later sent. Two ladders meant two answers for the same server.
     * </p>
     *
     * @param ver the formatted NMS integer for the running server
     * @return the module to construct through
     */
    public static @NotNull NmsBundle bundleFor(int ver) {
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
        // 1.17 runs Java 16, so this cannot be v1_18_R1 (floor 17).
        // Everything below 1.18.2 uses the shaded Adventure: those servers have no native
        // Adventure of their own to delegate to.
        if (ver < f("1.18.2")) { return NmsBundles.forModule("v1_17_R1"); }
        // 1.18.2 is where native MiniMessage arrives. Everything from here up uses the server's own
        // Adventure, not the relocated copy, which also means these servers never extract the nested
        // jar or build the child classloader. v1_18_R2 writes item names as legacy section codes
        // because ItemMeta.customName() does not exist until 1.21.4.
        if (ver < f("1.21.4")) { return NmsBundles.forModule("v1_18_R2"); }
        // 1.21.4+ can use the native Adventure apis, because that is where ItemMeta.customName()
        // arrived. That is v1_21_4, not v_latest: 1.21.4 runs Java 21 and v_latest targets 25.
        return NmsBundles.forModule("v1_21_4");
    }

    /**
     * Builds a component from Minecraft's JSON representation.
     *
     * <p>Replaced {@code fromInternalComponent(Component)}, removed 2026-08-30. That method required
     * the caller to construct a relocated Adventure component, which is impossible once the relocated
     * package is hidden, so it was unusable in the same way its counterpart was.
     */
    public @NotNull VersionedComponent fromJson(@NotNull String json) {
        Preconditions.checkNotNull(json, "json cannot be null");
        return bundleFor(NmsVersion.getFormattedNmsInteger()).componentFromJson(json);
    }

    public @NotNull VersionedComponent fromPlainText(@NotNull String text) {
        Preconditions.checkNotNull(text, "text cannot be null");
        return bundleFor(NmsVersion.getFormattedNmsInteger()).componentFromPlainText(text);
    }

    public @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage) {
        Preconditions.checkNotNull(miniMessage, "miniMessage cannot be null");
        return bundleFor(NmsVersion.getFormattedNmsInteger()).componentFromMiniMessage(miniMessage);
    }

    /**
     * MiniMessage with tag replacements.
     * <p>
     * Replaces the pattern of building an Adventure {@code TagResolver} at the call site and passing
     * the result through the removed fromInternalComponent, which required naming the shaded Adventure
     * copy from outside the {@code versions/*} modules.
     * </p>
     */
    public @NotNull VersionedComponent fromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders) {
        Preconditions.checkNotNull(miniMessage, "miniMessage cannot be null");
        Preconditions.checkNotNull(placeholders, "placeholders cannot be null");
        for (TextPlaceholder placeholder : placeholders) {
            Preconditions.checkNotNull(placeholder, "placeholders cannot contain null");
        }
        return bundleFor(NmsVersion.getFormattedNmsInteger()).componentFromMiniMessage(miniMessage, placeholders);
    }

    public @NotNull VersionedComponent fromLegacyAmpersand(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return bundleFor(NmsVersion.getFormattedNmsInteger()).componentFromLegacyAmpersand(legacy);
    }

    public @NotNull VersionedComponent fromLegacySection(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return bundleFor(NmsVersion.getFormattedNmsInteger()).componentFromLegacySection(legacy);
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
