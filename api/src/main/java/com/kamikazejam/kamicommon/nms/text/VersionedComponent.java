package com.kamikazejam.kamicommon.nms.text;

import java.util.Arrays;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
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
    @NotNull String serializePlainText();

    /**
     * Serializes the current message component to a string formatted using legacy ampersand (&amp;) color codes.
     */
    @NotNull String serializeLegacyAmpersand();

    /**
     * Serializes the current message component to a string formatted using legacy section (&sect;) color codes.
     */
    @NotNull String serializeLegacySection();

    /**
     * Serializes the current message component to a plain text string using the PlainTextComponentSerializer on the current platform.
     * @deprecated Replace with {@link #serializePlainText()}.
     */
    @Deprecated
    default @NotNull String plainText() {
        return this.serializePlainText();
    }

    /**
     * Create a new menu with the current message as the title, other arguments are passed as normal.
     * @param owner The inventory holder for this inventory. (inherited from Bukkit.createInventory)
     * @param size The size of the inventory. (inherited from Bukkit.createInventory)
     */
    @NotNull Inventory createInventory(@NotNull InventoryHolder owner, int size);

    /**
     * Create a new menu with the current message as the title, other arguments are passed as normal.
     * @param owner The inventory holder for this inventory. (inherited from Bukkit.createInventory)
     * @param type The type of the inventory. (inherited from Bukkit.createInventory)
     */
    @NotNull Inventory createInventory(@NotNull InventoryHolder owner, @NotNull InventoryType type);

    /**
     * Serializes to the JSON representation Minecraft itself uses for components.
     *
     * <p>This replaced {@code asInternalComponent()}, removed 2026-08-30. That method handed back the
     * relocated Adventure component, and once the relocated package is hidden from consumers it was
     * unusable by construction: a caller cannot name the type, cannot declare a variable for it, and
     * cannot cast it to anything. Assuming it is the server's own Adventure is wrong even on modern
     * versions, because it is the shaded copy.
     *
     * <p>JSON and MiniMessage carry the same information in a form callers can actually hold. On
     * 1.21.4 and up, {@code ModernVersionedComponent#asNativeComponent()} returns the server's real
     * Adventure component, which is nameable and is the right escape hatch there.
     */
    @NotNull String serializeJson();

    /**
     * Appends another VersionedComponent to this one, returning a new instance.<br>
     * The original instances are not modified.
     */
    @NotNull VersionedComponent append(@NotNull VersionedComponent other);

    /**
     * Returns a copy of this component with a click behaviour attached.
     * <p>
     * Use this rather than reaching for Adventure directly to call its
     * {@code clickEvent}. Anything outside the {@code versions/*} modules that names the shaded
     * Adventure copy pins it onto every server, including the modern ones that have Adventure
     * natively and do not need it.
     * </p>
     *
     * @param action what the click does
     * @param value  the command or URL the action applies to, without a leading slash for commands
     * @throws UnsupportedOperationException if this server version cannot express the action; see
     *                                       {@link ClickAction#COPY_TO_CLIPBOARD}
     */
    default @NotNull VersionedComponent click(@NotNull ClickAction action, @NotNull String value) {
        throw new UnsupportedOperationException(
                "click(ClickAction, String) is not implemented by " + this.getClass().getName());
    }

    /**
     * Returns a copy of this component with hover text attached.
     *
     * @param tooltip the component shown on hover
     */
    default @NotNull VersionedComponent hover(@NotNull VersionedComponent tooltip) {
        throw new UnsupportedOperationException(
                "hover(VersionedComponent) is not implemented by " + this.getClass().getName());
    }

    /**
     * Returns a copy of this component with a style flag explicitly set.
     * <p>
     * The common case is {@code decorate(TextDecoration.ITALIC, false)} before writing a component
     * into {@code ItemMeta}, because Minecraft italicises item names and lore automatically.
     * </p>
     */
    default @NotNull VersionedComponent decorate(@NotNull TextDecoration decoration, boolean value) {
        throw new UnsupportedOperationException(
                "decorate(TextDecoration, boolean) is not implemented by " + this.getClass().getName());
    }

    /**
     * Sends the current message component to multiple senders.
     */
    default void sendTo(@NotNull CommandSender... senders) {
        this.sendTo(Arrays.asList(senders));
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
