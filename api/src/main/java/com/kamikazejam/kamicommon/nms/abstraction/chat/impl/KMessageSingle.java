package com.kamikazejam.kamicommon.nms.abstraction.chat.impl;

import com.kamikazejam.kamicommon.actions.Action;
import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link KMessage} for single-line interactive messages.
 * <p>
 * This class represents a message consisting of a single line of text with
 * optional interactive actions. It provides methods for combining messages,
 * adding content, and creating messages with predefined interactive behaviors.
 * </p>
 * <p>
 * The class supports method chaining for fluent message construction and
 * includes static factory methods for creating common types of interactive
 * messages such as clickable commands, hover text, and URL links.
 * </p>
 */
@Setter
@Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class KMessageSingle extends KMessage {
    private @NotNull String line;

    /**
     * Creates a new empty single-line message.
     */
    public KMessageSingle() {
        super();
        this.line = "";
    }

    /**
     * Creates a new single-line message with the specified text.
     *
     * @param line the text content of the message
     */
    public KMessageSingle(@NotNull String line) {
        super();
        this.line = line;
    }

    /**
     * Creates a new single-line message with the specified text and actions.
     *
     * @param line the text content of the message
     * @param actions the {@link Action} instances to attach to this message
     */
    public KMessageSingle(@NotNull String line, @NotNull Action... actions) {
        super(actions);
        this.line = line;
    }

    /**
     * Creates a new single-line message with the specified text and action list.
     *
     * @param line the text content of the message
     * @param actions the list of {@link Action} instances to attach to this message
     */
    public KMessageSingle(@NotNull String line, @NotNull List<Action> actions) {
        super(actions);
        this.line = line;
    }

    /**
     * Adds another single-line message to this message.
     * <p>
     * This method concatenates the text content of the provided message
     * to this message's line and adds all of its actions to this message's
     * action list. This allows for combining multiple message parts into
     * a single cohesive message.
     * </p>
     *
     * @param message the {@link KMessageSingle} to add to this message
     * @return this {@link KMessageSingle} instance for method chaining
     */
    @NotNull
    public KMessageSingle add(@NotNull KMessageSingle message) {
        // Add the message's line and actions to this message
        this.line += message.getLine();
        super.addActions(message.getActions());
        return this;
    }

    /**
     * Adds text content to this message.
     * <p>
     * This method appends the provided content to the existing message line.
     * This is useful for building up message content incrementally or
     * adding dynamic content to existing messages.
     * </p>
     *
     * @param content the text content to append to this message
     * @return this {@link KMessageSingle} instance for method chaining
     */
    @NotNull
    public KMessageSingle add(@NotNull String content) {
        // Add the message's line and actions to this message
        this.line += content;
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For single-line messages, this returns a list containing only the
     * single line of text that makes up this message.
     * </p>
     */
    @Override
    public @NotNull List<String> getLines() {
        return new ArrayList<>(List.of(line));
    }

    /**
     * Creates a single-line message that executes a command when clicked.
     * <p>
     * This factory method creates a message where the entire line is clickable
     * and executes the specified command when clicked by the player. The command
     * will be run as if the player typed it in chat.
     * </p>
     *
     * @param line the text content to display and make clickable
     * @param command the command to execute when clicked (without leading slash)
     * @return a new {@link KMessageSingle} with the click action configured
     */
    @NotNull
    public static KMessageSingle ofClickRunCommand(@NotNull String line, @NotNull String command) {
        // Best way to apply an action to the entire string is with a placeholder and
        //  an action that's 'replacement' is the desired contents
        final String placeholder = "{cR_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setClickRunCommand(command));
    }

    /**
     * Creates a single-line message that suggests a command when clicked.
     * <p>
     * This factory method creates a message where the entire line is clickable
     * and suggests the specified command in the player's chat input when clicked.
     * The player can then modify or execute the suggested command.
     * </p>
     *
     * @param line the text content to display and make clickable
     * @param suggestion the command to suggest when clicked (without leading slash)
     * @return a new {@link KMessageSingle} with the click suggestion configured
     */
    @NotNull
    public static KMessageSingle ofClickSuggestCommand(@NotNull String line, @NotNull String suggestion) {
        final String placeholder = "{cS_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setClickSuggestCommand(suggestion));
    }

    /**
     * Creates a single-line message that opens a URL when clicked.
     * <p>
     * This factory method creates a message where the entire line is clickable
     * and opens the specified URL in the player's default web browser when clicked.
     * </p>
     *
     * @param line the text content to display and make clickable
     * @param url the URL to open when clicked
     * @return a new {@link KMessageSingle} with the URL click action configured
     */
    @NotNull
    public static KMessageSingle ofClickOpenURL(@NotNull String line, @NotNull String url) {
        final String placeholder = "{cO_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setClickOpenURL(url));
    }

    /**
     * Creates a single-line message with hover text.
     * <p>
     * This factory method creates a message where the entire line shows
     * custom hover text when the player hovers their cursor over it.
     * </p>
     *
     * @param line the text content to display and make hoverable
     * @param text the text to show in the hover tooltip
     * @return a new {@link KMessageSingle} with the hover text configured
     */
    @NotNull
    public static KMessageSingle ofHoverText(@NotNull String line, @NotNull String text) {
        final String placeholder = "{hT_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setHoverText(text));
    }

    /**
     * Creates a single-line message with hover item display.
     * <p>
     * This factory method creates a message where the entire line shows
     * an item tooltip when the player hovers their cursor over it.
     * </p>
     *
     * @param line the text content to display and make hoverable
     * @param item the {@link ItemStack} to show in the hover tooltip
     * @return a new {@link KMessageSingle} with the hover item configured
     */
    @NotNull
    public static KMessageSingle ofHoverItem(@NotNull String line, @NotNull ItemStack item) {
        final String placeholder = "{hI_" + UUID.randomUUID() + "}";
        return new KMessageSingle(placeholder, new Action(placeholder, line).setHoverItem(item));
    }
}
