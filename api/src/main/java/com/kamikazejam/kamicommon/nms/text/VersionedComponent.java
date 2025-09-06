package com.kamikazejam.kamicommon.nms.text;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * A multi-version wrapper that supports Kyori Adventure Components and the MiniMessage format.<br>
 * <br>
 * This wrapper facilitates sending this wrapped component despite server version differences. (See {@link #sendTo(CommandSender)})<br>
 * On older servers, it likely sends via BaseComponents, while on newer servers it can use the native adventure api.<br>
 * <br>
 * This wrapper attempts to use the native adventure api on newer servers, but falls back to using an internal shaded copy of adventure
 * if the server does not support it natively.
 */
@SuppressWarnings("unused")
public interface VersionedComponent {

    /**
     * Sends the current message component to a sender.
     */
    void sendTo(@NotNull CommandSender sender);

    /**
     * Serializes the current message component to a MiniMessage string.
     */
    @NotNull String serializeMiniMessage();

    /**
     * Serializes the current message component to a plain text string using the PlainTextComponentSerializer on the current platform.
     */
    @NotNull String plainText();

    /**
     * Sends the current message component to multiple senders.
     */
    default void sendTo(@NotNull CommandSender... senders) {
        this.sendTo(List.of(senders));
    }

    /**
     * Sends the current message component to multiple senders.
     */
    default void sendTo(@NotNull Collection<CommandSender> senders) {
        for (CommandSender sender : senders) {
            this.sendTo(sender);
        }
    }
}
