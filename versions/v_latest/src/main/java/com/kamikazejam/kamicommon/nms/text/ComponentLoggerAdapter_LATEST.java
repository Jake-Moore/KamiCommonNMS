package com.kamikazejam.kamicommon.nms.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Confirmed For: 1_18_R2 --> 1.21.8 (latest)
 */
public class ComponentLoggerAdapter_LATEST implements ComponentLoggerAdapter {
    public void log(@NotNull Plugin plugin, @NotNull VersionedComponent message, @NotNull Level level) {
        // This is the new implementation for versions after paper added adventure support
        // It uses the built-in component logger to send colored components to the console directly
        Component nativeComponent = getNativeComponent(message);

        if (level == Level.FINE) {
            plugin.getComponentLogger().debug(nativeComponent);
        } else if (level == Level.INFO) {
            plugin.getComponentLogger().info(nativeComponent);
        } else if (level == Level.WARNING) {
            plugin.getComponentLogger().warn(nativeComponent);
        } else if (level == Level.SEVERE) {
            plugin.getComponentLogger().error(nativeComponent);
        } else {
            // Can't log at custom levels, error out to let author know
            throw new UnsupportedOperationException("Unsupported log level: " + level.getName());
        }
    }

    @NotNull
    private Component getNativeComponent(VersionedComponent message) {
        if (message instanceof  ModernVersionedComponent modern) {
            return modern.asNativeComponent();
        }
        // Fallback to mini message adapter
        String miniMessage = message.serializeMiniMessage();
        return MiniMessage.miniMessage().deserialize(miniMessage);
    }
}
