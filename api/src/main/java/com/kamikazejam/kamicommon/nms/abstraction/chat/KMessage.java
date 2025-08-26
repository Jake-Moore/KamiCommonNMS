package com.kamikazejam.kamicommon.nms.abstraction.chat;

import com.kamikazejam.kamicommon.actions.Action;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for interactive messages that can contain rich text,
 * actions, and events for enhanced player communication.
 * <p>
 * This class provides the foundation for creating interactive messages with
 * hover text, click actions, and other rich formatting features. Messages
 * support a placeholder-based action system where specific text patterns
 * are replaced with interactive elements during processing.
 * </p>
 * <p>
 * All methods in this class support method chaining for fluent message
 * construction. The class handles both translation settings and action
 * management, providing a complete framework for interactive messaging.
 * </p>
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class KMessage {
    private final @NotNull List<Action> actions = new ArrayList<>();
    @Setter
    private boolean translate = true;

    /**
     * Creates a new empty message with no actions.
     */
    public KMessage() {}

    /**
     * Creates a new message with the specified actions.
     *
     * @param actions the {@link Action} instances to attach to this message
     */
    public KMessage(@NotNull Action... actions) {
        this.actions.addAll(Arrays.asList(actions));
    }

    /**
     * Creates a new message with the specified list of actions.
     *
     * @param actions the list of {@link Action} instances to attach to this message
     */
    public KMessage(@NotNull List<Action> actions) {
        this.actions.addAll(actions);
    }

    /**
     * Retrieves the lines of text that make up this message.
     * <p>
     * This abstract method must be implemented by subclasses to define
     * how the message content is structured. For single-line messages,
     * this returns a list with one element. For multi-line messages,
     * this returns all lines in order.
     * </p>
     *
     * @return the list of text lines that make up this message
     */
    @NotNull
    public abstract List<String> getLines();

    /**
     * Adds a single action to this message.
     * <p>
     * Actions define interactive elements like hover text, click events,
     * and other rich formatting features. The action's placeholder will
     * be replaced with its replacement text during message processing.
     * </p>
     *
     * @param action the {@link Action} to add to this message
     * @return this {@link KMessage} instance for method chaining
     */
    @NotNull
    public final KMessage addAction(@NotNull Action action) {
        actions.add(action);
        return this;
    }

    /**
     * Adds multiple actions to this message.
     * <p>
     * This method adds all actions from the provided list to this message's
     * action collection. Each action will be processed during message sending.
     * </p>
     *
     * @param actions the list of {@link Action} instances to add
     * @return this {@link KMessage} instance for method chaining
     */
    @NotNull
    public final KMessage addActions(@NotNull List<Action> actions) {
        this.actions.addAll(actions);
        return this;
    }

    /**
     * Adds a hover item action with the specified placeholder and replacement.
     * <p>
     * This convenience method creates and adds an {@link Action} that displays
     * an item tooltip when the replacement text is hovered over. The placeholder
     * in the message will be replaced with the replacement text.
     * </p>
     *
     * @param placeholder the placeholder text to replace in the message
     * @param replacement the text to display in place of the placeholder
     * @param item the {@link ItemStack} to show in the hover tooltip
     * @return this {@link KMessage} instance for method chaining
     */
    @NotNull
    public final KMessage addHoverItem(@NotNull String placeholder, @NotNull String replacement, @NotNull ItemStack item) {
        return this.addAction(new Action(placeholder, replacement).setHoverItem(item));
    }

    /**
     * Adds a click-to-run-command action with the specified placeholder and replacement.
     * <p>
     * This convenience method creates and adds an {@link Action} that executes
     * a command when the replacement text is clicked. The command will be run
     * as if the player typed it in chat.
     * </p>
     *
     * @param placeholder the placeholder text to replace in the message
     * @param replacement the text to display in place of the placeholder
     * @param command the command to execute when clicked (without leading slash)
     * @return this {@link KMessage} instance for method chaining
     */
    @NotNull
    public final KMessage addClickRunCommand(@NotNull String placeholder, @NotNull String replacement, @NotNull String command) {
        return this.addAction(new Action(placeholder, replacement).setClickRunCommand(command));
    }

    /**
     * Adds a click-to-suggest-command action with the specified placeholder and replacement.
     * <p>
     * This convenience method creates and adds an {@link Action} that suggests
     * a command in the player's chat input when the replacement text is clicked.
     * The player can then modify or execute the suggested command.
     * </p>
     *
     * @param placeholder the placeholder text to replace in the message
     * @param replacement the text to display in place of the placeholder
     * @param suggestion the command to suggest when clicked (without leading slash)
     * @return this {@link KMessage} instance for method chaining
     */
    @NotNull
    public final KMessage addClickSuggestCommand(@NotNull String placeholder, @NotNull String replacement, @NotNull String suggestion) {
        return this.addAction(new Action(placeholder, replacement).setClickSuggestCommand(suggestion));
    }

    /**
     * Adds a click-to-open-URL action with the specified placeholder and replacement.
     * <p>
     * This convenience method creates and adds an {@link Action} that opens
     * a URL in the player's default web browser when the replacement text is clicked.
     * </p>
     *
     * @param placeholder the placeholder text to replace in the message
     * @param replacement the text to display in place of the placeholder
     * @param url the URL to open when clicked
     * @return this {@link KMessage} instance for method chaining
     */
    @NotNull
    public final KMessage addClickOpenURL(@NotNull String placeholder, @NotNull String replacement, @NotNull String url) {
        return this.addAction(new Action(placeholder, replacement).setClickOpenURL(url));
    }

    /**
     * Adds a hover text action with the specified placeholder and replacement.
     * <p>
     * This convenience method creates and adds an {@link Action} that displays
     * custom text in a tooltip when the replacement text is hovered over.
     * </p>
     *
     * @param placeholder the placeholder text to replace in the message
     * @param replacement the text to display in place of the placeholder
     * @param text the text to show in the hover tooltip
     * @return this {@link KMessage} instance for method chaining
     */
    @NotNull
    public final KMessage addHoverText(@NotNull String placeholder, @NotNull String replacement, @NotNull String text) {
        return this.addAction(new Action(placeholder, replacement).setHoverText(text));
    }
}
