package com.kamikazejam.kamicommon.nms.text;

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
    @NotNull String plainText();

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
