package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.minimessage.MiniMessage;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Confirmed For: 1_8_R1, 1_8_R2, 1_8_R3, 1_9_R1, 1_9_R2, 1_10_R1, 1_11_R1
 */
public class VersionedComponent_1_11_R1 implements VersionedComponent {
    private final @NotNull Component component;
    public VersionedComponent_1_11_R1(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public void sendTo(@NotNull CommandSender sender) {
        if (sender instanceof Player player) {
            // Use direct spigot method
            player.spigot().sendMessage(BungeeComponentSerializer.legacy().serialize(this.component));
        } else {
            // Wrap into legacy string format to use String message method
            BaseComponent[] baseComponents = BungeeComponentSerializer.legacy().serialize(this.component);
            sender.sendMessage((new TextComponent(baseComponents)).toLegacyText());
        }
    }

    @Override
    public @NotNull String serializeMiniMessage() {
        return MiniMessage.miniMessage().serialize(this.component);
    }

    @Override
    public @NotNull String plainText() {
        return PlainTextComponentSerializer.plainText().serialize(this.component);
    }
}
