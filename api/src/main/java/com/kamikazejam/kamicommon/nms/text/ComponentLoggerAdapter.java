package com.kamikazejam.kamicommon.nms.text;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public interface ComponentLoggerAdapter {
    /**
     * Logs a {@link VersionedComponent} message to the server console for a specific plugin at the given log level.
     */
    void log(@NotNull Plugin plugin, @NotNull VersionedComponent message, @NotNull Level level);
}
