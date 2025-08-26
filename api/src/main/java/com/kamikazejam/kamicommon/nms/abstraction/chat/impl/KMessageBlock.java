package com.kamikazejam.kamicommon.nms.abstraction.chat.impl;

import com.kamikazejam.kamicommon.actions.Action;
import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link KMessage} for multi-line interactive messages.
 * <p>
 * This class represents a message consisting of multiple lines of text with
 * optional interactive actions. It provides methods for adding lines dynamically
 * and includes static factory methods for creating common types of interactive
 * multi-line messages such as clickable commands, hover text, and URL links.
 * </p>
 * <p>
 * The class supports method chaining for fluent message construction and handles
 * line management for complex multi-line displays like help text, menus, or
 * information panels.
 * </p>
 */
@Setter
@Getter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class KMessageBlock extends KMessage {
    private @NotNull List<String> lines = new ArrayList<>();

    /**
     * Creates a new multi-line message with a single initial line.
     *
     * @param line the initial line of text for the message
     */
    public KMessageBlock(@NotNull String line) {
        super();
        this.lines.add(line);
    }

    /**
     * Creates a new multi-line message with the specified lines.
     * <p>
     * <strong>Note:</strong> This constructor converts the varargs array to a string
     * representation and adds it as a single line. For multiple separate lines,
     * use the {@link List} constructor instead.
     * </p>
     *
     * @param lines the lines to include in the message
     */
    public KMessageBlock(@NotNull String... lines) {
        super();
        this.lines.add(Arrays.toString(lines));
    }

    /**
     * Creates a new multi-line message with the specified list of lines.
     *
     * @param lines the list of text lines for the message
     */
    public KMessageBlock(@NotNull List<String> lines) {
        super();
        this.lines.addAll(lines);
    }

    /**
     * Creates a new multi-line message with a single line and actions.
     *
     * @param line the initial line of text for the message
     * @param actions the {@link Action} instances to attach to this message
     */
    public KMessageBlock(@NotNull String line, @NotNull Action... actions) {
        super(actions);
        this.lines.add(line);
    }

    /**
     * Creates a new multi-line message with a single line and action list.
     *
     * @param line the initial line of text for the message
     * @param actions the list of {@link Action} instances to attach to this message
     */
    public KMessageBlock(@NotNull String line, @NotNull List<Action> actions) {
        super(actions);
        this.lines.add(line);
    }

    /**
     * Creates a new multi-line message with the specified lines and actions.
     *
     * @param lines the list of text lines for the message
     * @param actions the {@link Action} instances to attach to this message
     */
    public KMessageBlock(@NotNull List<String> lines, @NotNull Action... actions) {
        super(actions);
        this.lines.addAll(lines);
    }

    /**
     * Creates a new multi-line message with the specified lines and action list.
     *
     * @param lines the list of text lines for the message
     * @param actions the list of {@link Action} instances to attach to this message
     */
    public KMessageBlock(@NotNull List<String> lines, @NotNull List<Action> actions) {
        super(actions);
        this.lines.addAll(lines);
    }

    /**
     * Adds a new line to this multi-line message.
     * <p>
     * This method appends the specified line to the end of the current line list.
     * This is useful for building up message content dynamically or adding
     * lines based on runtime conditions.
     * </p>
     *
     * @param line the text line to add to this message
     * @return this {@link KMessageBlock} instance for method chaining
     */
    @NotNull
    public KMessageBlock addLine(@NotNull String line) {
        lines.add(line);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For multi-line messages, this returns the complete list of lines
     * that make up this message block.
     * </p>
     */
    @Override
    public @NotNull List<String> getLines() {
        return new ArrayList<>(lines);
    }

    /**
     * Creates a multi-line message that executes a command when clicked.
     * <p>
     * This factory method creates a message where the entire content is clickable
     * and executes the specified command when clicked by the player. The command
     * will be run as if the player typed it in chat.
     * </p>
     *
     * @param line the text content to display and make clickable
     * @param command the command to execute when clicked (without leading slash)
     * @return a new {@link KMessageBlock} with the click action configured
     */
    @NotNull
    public static KMessageBlock ofClickRunCommand(@NotNull String line, @NotNull String command) {
        // Best way to apply an action to the entire string is with a placeholder and
        //  an action that's 'replacement' is the desired contents
        final String placeholder = "{cR_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setClickRunCommand(command));
    }

    /**
     * Creates a multi-line message that suggests a command when clicked.
     * <p>
     * This factory method creates a message where the entire content is clickable
     * and suggests the specified command in the player's chat input when clicked.
     * The player can then modify or execute the suggested command.
     * </p>
     *
     * @param line the text content to display and make clickable
     * @param suggestion the command to suggest when clicked (without leading slash)
     * @return a new {@link KMessageBlock} with the click suggestion configured
     */
    @NotNull
    public static KMessageBlock ofClickSuggestCommand(@NotNull String line, @NotNull String suggestion) {
        final String placeholder = "{cS_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setClickSuggestCommand(suggestion));
    }

    /**
     * Creates a multi-line message that opens a URL when clicked.
     * <p>
     * This factory method creates a message where the entire content is clickable
     * and opens the specified URL in the player's default web browser when clicked.
     * </p>
     *
     * @param line the text content to display and make clickable
     * @param url the URL to open when clicked
     * @return a new {@link KMessageBlock} with the URL click action configured
     */
    @NotNull
    public static KMessageBlock ofClickOpenURL(@NotNull String line, @NotNull String url) {
        final String placeholder = "{cO_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setClickOpenURL(url));
    }

    /**
     * Creates a multi-line message with hover text.
     * <p>
     * This factory method creates a message where the entire content shows
     * custom hover text when the player hovers their cursor over it.
     * </p>
     *
     * @param line the text content to display and make hoverable
     * @param text the text to show in the hover tooltip
     * @return a new {@link KMessageBlock} with the hover text configured
     */
    @NotNull
    public static KMessageBlock ofHoverText(@NotNull String line, @NotNull String text) {
        final String placeholder = "{hT_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setHoverText(text));
    }

    /**
     * Creates a multi-line message with hover item display.
     * <p>
     * This factory method creates a message where the entire content shows
     * an item tooltip when the player hovers their cursor over it.
     * </p>
     *
     * @param line the text content to display and make hoverable
     * @param item the {@link ItemStack} to show in the hover tooltip
     * @return a new {@link KMessageBlock} with the hover item configured
     */
    @NotNull
    public static KMessageBlock ofHoverItem(@NotNull String line, @NotNull ItemStack item) {
        final String placeholder = "{hI_" + UUID.randomUUID() + "}";
        return new KMessageBlock(placeholder, new Action(placeholder, line).setHoverItem(item));
    }
}
