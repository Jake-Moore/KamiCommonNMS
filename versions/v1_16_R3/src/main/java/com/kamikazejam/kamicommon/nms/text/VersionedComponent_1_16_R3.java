package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.MiniMessage;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Confirmed For: 1_16_R1, 1_16_R2, 1_16_R3
 * <br>
 * Uses BungeeComponentSerializer.get() since 1.16 added hex color support
 */
public class VersionedComponent_1_16_R3 implements VersionedComponent {
    private final @NotNull Component component;
    public VersionedComponent_1_16_R3(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public void sendTo(@NotNull CommandSender sender) {
        // Use direct spigot method
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
}
