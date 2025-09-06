package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.util.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * Confirmed For: 1_18_R2 to 1.21.8 (latest)
 * <br>
 * 1_18_R2 was the first version of paper to ship with kyori adventure **MiniMessage** support.
 */
public class VersionedComponent_LATEST implements VersionedComponent {
    private final @NotNull Component component;
    private VersionedComponent_LATEST(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public void sendTo(@NotNull CommandSender sender) {
        // Send component directly using adventure method
        sender.sendMessage(this.component);
    }

    @Override
    public @NotNull String serializeMiniMessage() {
        return MiniMessage.miniMessage().serialize(component);
    }

    @Override
    public @NotNull String plainText() {
        return PlainTextComponentSerializer.plainText().serialize(this.component);
    }

    @Internal
    public static @NotNull VersionedComponent_LATEST fromMiniMessage(@NotNull String miniMessage) {
        Preconditions.checkNotNull(miniMessage, "miniMessage cannot be null");
        return new VersionedComponent_LATEST(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    @Internal
    public static @NotNull VersionedComponent_LATEST fromLegacyAmpersand(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return new VersionedComponent_LATEST(LegacyComponentSerializer.legacyAmpersand().deserialize(legacy));
    }

    @Internal
    public static @NotNull VersionedComponent_LATEST fromLegacySection(@NotNull String legacy) {
        Preconditions.checkNotNull(legacy, "legacy cannot be null");
        return new VersionedComponent_LATEST(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }
}
