package com.kamikazejam.kamicommon.nms.text;

/**
 * A style flag that can be turned on or off on a {@link VersionedComponent}.
 * <p>
 * The one that matters in practice is {@link #ITALIC}. Minecraft italicises item names and lore
 * automatically, so anything rendering a component into {@code ItemMeta} generally wants to set it
 * explicitly to {@code false} to have MiniMessage formatting represented exactly as written.
 * </p>
 * <p>
 * Named here rather than reusing the shaded Adventure enum so that callers never reference the
 * shaded copy. See {@link ClickAction} for why that matters.
 * </p>
 */
public enum TextDecoration {
    BOLD,
    ITALIC,
    UNDERLINED,
    STRIKETHROUGH,
    OBFUSCATED
}
