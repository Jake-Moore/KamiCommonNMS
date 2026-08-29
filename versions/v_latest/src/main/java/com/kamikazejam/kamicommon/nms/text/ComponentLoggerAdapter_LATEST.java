package com.kamikazejam.kamicommon.nms.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * The 26.x twin of {@code ComponentLoggerAdapter_1_18_R2} in {@code versions/v1_18_R2}, and the only reason it exists is to be
 * compiled.
 * <p>
 * Nothing dispatches here. The ladder sends every server, 26.x included, to the v1_18_R2 copy, because
 * under this project's convention a class lives in the module named for the FIRST version it works
 * on. That is correct for dispatch and bad for early warning: it means the implementation is only
 * ever compiled against an old dev bundle, so an API this code uses could be removed in 26.x and the
 * build would not notice until a server did.
 * </p><p>
 * This copy closes that. It compiles against {@code highestPaperDep}, so bumping that version
 * compile-checks this capability against bleeding-edge Paper. If it stops compiling, that IS the
 * finding: fix it here and in the twin, and add a ladder branch if the two must now differ.
 * </p>
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
        if (message instanceof ModernVersionedComponent) {
            ModernVersionedComponent modern = (ModernVersionedComponent) message;
            return modern.asNativeComponent();
        }
        // Fallback to mini message adapter
        String miniMessage = message.serializeMiniMessage();
        return MiniMessage.miniMessage().deserialize(miniMessage);
    }
}
