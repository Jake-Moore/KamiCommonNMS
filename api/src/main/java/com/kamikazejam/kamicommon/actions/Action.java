package com.kamikazejam.kamicommon.actions;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for basic expression of chat message actions.
 * Extends StandaloneAction, adds hover item chat action {@link HoverItem}
 */
@SuppressWarnings("unused")
public class Action extends StandaloneAction {
    /**
     * Create a new empty Action with a placeholder and its replacement text.<br>
     * Use setters to add actions to this object.
     *
     * @param placeholder The placeholder in the message string to replace
     * @param replacement The text to replace the placeholder with
     */
    public Action(@NotNull String placeholder, @NotNull String replacement) {
        super(placeholder, replacement);
    }

    /**
     * @param item The {@link ItemStack} to show when hovering over this action's text
     */
    public Action setHoverItem(@NotNull ItemStack item) {
        this.hover = new HoverItem(item);
        return this;
    }

    /**
     * Note: the command does not need to start with a slash (/).
     * It will be automatically added if necessary.
     *
     * @param command The command to run, when this action's text is clicked.
     */
    @Override
    public Action setClickRunCommand(@NotNull String command) {
        super.setClickRunCommand(command);
        return this;
    }

    /**
     * @param suggestion The chatbox text to suggest to the player, when this action's text is clicked
     */
    @Override
    public Action setClickSuggestCommand(@NotNull String suggestion) {
        super.setClickSuggestCommand(suggestion);
        return this;
    }

    /**
     * @param url The url to open, when this action's text is clicked
     */
    @Override
    public Action setClickOpenURL(@NotNull String url) {
        super.setClickOpenURL(url);
        return this;
    }

    /**
     * @param text The text to show when this action's text is hovered over
     */
    @Override
    public Action setHoverText(@NotNull String text) {
        super.setHoverText(text);
        return this;
    }
}
