package com.kamikazejam.kamicommon.nms.text;

/**
 * A click behaviour that can be attached to a {@link VersionedComponent}.
 * <p>
 * This exists so that callers can build clickable text without naming an Adventure type. The shaded
 * Adventure copy is an implementation detail of the {@code versions/*} modules: it is present only
 * for servers with no native Adventure, and anything above this module that referenced it would pin
 * it onto every server. See {@code VersionedComponent#click}.
 * </p>
 */
public enum ClickAction {
    /** Runs the value as a command as the clicking player. */
    RUN_COMMAND,
    /** Places the value in the player's chat input without sending it. */
    SUGGEST_COMMAND,
    /** Opens the value as a URL. */
    OPEN_URL,
    /**
     * Copies the value to the player's clipboard.
     * <p>
     * <b>Not available before 1.16.</b> Implementations below that version throw
     * {@link UnsupportedOperationException} naming the server version rather than dropping the
     * behaviour silently, because a click that quietly does nothing is worse than one that fails
     * loudly during development.
     * </p>
     */
    COPY_TO_CLIPBOARD
}
