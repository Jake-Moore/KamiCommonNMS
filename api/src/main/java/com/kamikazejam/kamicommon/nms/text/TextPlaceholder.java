package com.kamikazejam.kamicommon.nms.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A MiniMessage tag replacement, expressed as data rather than as a wrapped Adventure resolver.
 * <p>
 * Pass these to {@code VersionedComponentSerializer#fromMiniMessage(String, TextPlaceholder...)}.
 * The {@code versions/*} module converts them into whichever resolver its server needs, which is the
 * point: <b>this class deliberately holds no Adventure type</b>. Wrapping a {@code TagResolver} here
 * would be more convenient and would move the leak rather than close it, invisibly, because the field
 * would be private.
 * </p>
 * <p>
 * The three factories mirror the three Adventure offers: an unparsed literal, a nested MiniMessage
 * string, and an already-built component.
 * </p>
 */
public final class TextPlaceholder {

    /** Which of the three forms a placeholder carries. */
    public enum Kind {
        /** The value is inserted literally; any MiniMessage syntax in it is <b>not</b> parsed. */
        LITERAL,
        /** The value is parsed as MiniMessage before insertion. */
        MINI_MESSAGE,
        /** The value is an already-built component. */
        COMPONENT
    }

    private final @NotNull String key;
    private final @NotNull Kind kind;
    private final @Nullable String stringValue;
    private final @Nullable VersionedComponent componentValue;

    private TextPlaceholder(@NotNull String key, @NotNull Kind kind,
                            @Nullable String stringValue, @Nullable VersionedComponent componentValue) {
        if (key == null) { throw new IllegalArgumentException("key cannot be null"); }
        if (key.isEmpty()) { throw new IllegalArgumentException("key cannot be empty"); }
        this.key = key;
        this.kind = kind;
        this.stringValue = stringValue;
        this.componentValue = componentValue;
    }

    /**
     * A replacement inserted verbatim. MiniMessage syntax inside {@code value} is not interpreted,
     * which is what you want for anything user-supplied.
     *
     * @param key the tag name, without angle brackets
     */
    public static @NotNull TextPlaceholder literal(@NotNull String key, @NotNull String value) {
        if (value == null) { throw new IllegalArgumentException("value cannot be null for key: " + key); }
        return new TextPlaceholder(key, Kind.LITERAL, value, null);
    }

    /**
     * A replacement parsed as MiniMessage before insertion.
     *
     * @param key the tag name, without angle brackets
     */
    public static @NotNull TextPlaceholder miniMessage(@NotNull String key, @NotNull String value) {
        if (value == null) { throw new IllegalArgumentException("value cannot be null for key: " + key); }
        return new TextPlaceholder(key, Kind.MINI_MESSAGE, value, null);
    }

    /**
     * A replacement that is an already-built component, so it can carry its own click and hover
     * behaviour. This is the form that replaces reaching through
     * {@code VersionedComponent#asInternalComponent()} to build an Adventure {@code Placeholder}.
     *
     * @param key the tag name, without angle brackets
     */
    public static @NotNull TextPlaceholder component(@NotNull String key, @NotNull VersionedComponent value) {
        if (value == null) { throw new IllegalArgumentException("value cannot be null for key: " + key); }
        return new TextPlaceholder(key, Kind.COMPONENT, null, value);
    }

    /** The tag name, without angle brackets. */
    public @NotNull String getKey() { return this.key; }

    /** Which form this placeholder carries. */
    public @NotNull Kind getKind() { return this.kind; }

    /** The string value for {@link Kind#LITERAL} and {@link Kind#MINI_MESSAGE}, else {@code null}. */
    public @Nullable String getStringValue() { return this.stringValue; }

    /** The component value for {@link Kind#COMPONENT}, else {@code null}. */
    public @Nullable VersionedComponent getComponentValue() { return this.componentValue; }

    @Override
    public @NotNull String toString() {
        return "TextPlaceholder{" + this.key + ", " + this.kind + "}";
    }
}
