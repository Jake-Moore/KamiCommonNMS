package com.kamikazejam.kamicommon.nms.abstraction.chat;

import com.kamikazejam.kamicommon.actions.Action;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageBlock;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Abstract class providing version-specific implementations for processing and
 * sending interactive messages with actions and events.
 * <p>
 * This abstraction handles the processing of {@link KMessage} instances, including
 * placeholder replacement, action attachment, and version-appropriate message
 * delivery. It supports both single-line and multi-line messages with rich
 * interactive features like hover text, click actions, and item displays.
 * </p>
 * <p>
 * The manager processes messages by replacing placeholders with their corresponding
 * {@link Action} replacements, attaching interactive events, and then sending the
 * final message to the recipient using version-specific methods.
 * </p>
 */
@SuppressWarnings("unused")
public abstract class AbstractMessageManager {

    /**
     * @hidden
     */
    protected abstract void processAndSendInternal(@NotNull CommandSender sender, @NotNull KMessage message);

    /**
     * Processes and sends a message with all actions and replacements applied.
     * <p>
     * This method handles the complete message processing pipeline
     * including placeholder replacement, action processing,
     * and final delivery to the recipient.
     * </p>
     *
     * @param sender the {@link CommandSender} to send the message to
     * @param message the {@link KMessage} to process and send
     */
    public void processAndSend(@NotNull CommandSender sender, @NotNull KMessage message) {
        this.processAndSendInternal(sender, message);
    }

    /**
     * Processes and sends multiple messages sequentially.
     * <p>
     * This convenience method processes each message in the list individually,
     * maintaining the order of messages. Each message is fully processed
     * before the next one is handled.
     * </p>
     *
     * @param sender the {@link CommandSender} to send the messages to
     * @param messages the list of {@link KMessage} instances to process and send
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull List<KMessage> messages) {
        messages.forEach(message -> this.processAndSendInternal(sender, message));
    }

    /**
     * Processes and sends multiple messages provided as varargs.
     * <p>
     * This convenience method converts the varargs array to a list and
     * processes each message sequentially. It provides a fluent interface
     * for sending multiple messages without creating explicit collections.
     * </p>
     *
     * @param sender the {@link CommandSender} to send the messages to
     * @param messages the {@link KMessage} instances to process and send
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull KMessage... messages) {
        this.processAndSend(sender, List.of(messages));
    }

    /**
     * Processes and sends a single line message with the specified actions.
     * <p>
     * This convenience method creates a {@link KMessageSingle} from the provided
     * line and actions, then processes and sends it. This is useful for simple
     * single-line messages with interactive elements.
     * </p>
     *
     * @param sender the {@link CommandSender} to send the message to
     * @param line the text content of the message
     * @param actions the {@link Action} instances to attach to the message
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull String line, @NotNull Action... actions) {
        this.processAndSendInternal(sender, new KMessageSingle(line, actions));
    }

    /**
     * Processes and sends a single line message with translation control.
     * <p>
     * This convenience method creates a {@link KMessageSingle} with the specified
     * translation setting. The translation flag determines whether color codes
     * and other formatting should be processed during message sending.
     * </p>
     *
     * @param sender the {@link CommandSender} to send the message to
     * @param line the text content of the message
     * @param translate whether to process formatting codes and translations
     * @param actions the {@link Action} instances to attach to the message
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull String line, boolean translate, @NotNull Action... actions) {
        this.processAndSendInternal(sender, new KMessageSingle(line).setTranslate(translate));
    }

    /**
     * Processes and sends a multi-line message with the specified actions.
     * <p>
     * This convenience method creates a {@link KMessageBlock} from the provided
     * lines and actions, then processes and sends it. This is useful for
     * multi-line messages like help text or information displays.
     * </p>
     *
     * @param sender the {@link CommandSender} to send the message to
     * @param lines the list of text lines that make up the message
     * @param actions the {@link Action} instances to attach to the message
     */
    public final void processAndSend(@NotNull CommandSender sender, @NotNull List<String> lines, @NotNull Action... actions) {
        this.processAndSendInternal(sender, new KMessageBlock(lines, actions));
    }
}
