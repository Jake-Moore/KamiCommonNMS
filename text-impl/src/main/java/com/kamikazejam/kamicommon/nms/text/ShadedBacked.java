package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * How the relocated-Adventure implementations reach each other's component.
 *
 * <p><b>Package-private on purpose, and only meaningful inside the nested jar.</b> This replaced
 * {@code VersionedComponent#asInternalComponent()}, which was removed from the public interface on
 * 2026-08-30. That method returned a relocated Adventure component to callers who, once the relocated
 * package is hidden, cannot name the type, cannot declare a variable for it and cannot cast it. It
 * was unusable by construction. Every real use of it was this: one implementation reaching into
 * another, which is now an internal concern of this module.
 */
interface ShadedBacked {

    @NotNull Component shadedComponent();

    /**
     * The relocated component behind any {@link VersionedComponent}.
     *
     * <p>Falls back to a JSON round-trip for a foreign implementation. A native-tier component from
     * {@code v1_21_4} or {@code v_latest} is not {@code ShadedBacked}, and JSON is the only
     * representation both sides can hold without naming each other's types.
     */
    static @NotNull Component of(@NotNull VersionedComponent other) {
        if (other instanceof ShadedBacked) {
            return ((ShadedBacked) other).shadedComponent();
        }
        return JSONComponentSerializer.json().deserialize(other.serializeJson());
    }
}
