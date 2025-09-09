package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.MiniMessage;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Confirmed For: 1_17_R1, 1_18_R1
 */
public class VersionedComponent_1_18_R1 implements VersionedComponent {
    final @NotNull Component component;
    public VersionedComponent_1_18_R1(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public void sendTo(@NotNull CommandSender sender) {
        // Use direct spigot method (deprecated, but lacks MiniMessage in 1.17 to fix)
        //   Have to use this because paper apis don't allow converting MiniMessage strings yet
        //    and thus we cannot obtain the right Component instance to use the paper method.
        sender.spigot().sendMessage(BungeeComponentSerializer.get().serialize(this.component));
    }

    @Override
    public @NotNull String serializeMiniMessage() {
        return MiniMessage.miniMessage().serialize(component);
    }

    @Override
    public @NotNull String plainText() {
        return PlainTextComponentSerializer.plainText().serialize(this.component);
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder owner, int size) {
        // Use direct spigot method (deprecated, but lacks MiniMessage in 1.17 to fix)
        //   Have to use this because paper apis don't allow converting MiniMessage strings yet
        //    and thus we cannot obtain the right Component instance to use the paper method.
        // Needs to be serialized into legacy string that contains section symbols for title
        String title = LegacyComponentSerializer.legacySection().serialize(this.component);
        return Bukkit.createInventory(owner, size, title);
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder owner, @NotNull InventoryType type) {
        // Use direct spigot method (deprecated, but lacks MiniMessage in 1.17 to fix)
        //   Have to use this because paper apis don't allow converting MiniMessage strings yet
        //    and thus we cannot obtain the right Component instance to use the paper method.
        // Needs to be serialized into legacy string that contains section symbols for title
        String title = LegacyComponentSerializer.legacySection().serialize(this.component);
        return Bukkit.createInventory(owner, type, title);
    }
}
