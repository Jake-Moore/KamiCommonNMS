package com.kamikazejam.kamicommon.nms.text;

import com.kamikazejam.kamicommon.util.LegacyColors;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Confirmed For: 1_8_R1 --> 1_18_R1
 */
public class ComponentLoggerAdapter_1_18_R1 implements ComponentLoggerAdapter {
    public void log(@NotNull Plugin plugin, @NotNull VersionedComponent message, @NotNull Level level) {
        // This is an implementation for versions before paper added adventure support
        // It uses the old (backwards compatible) way of sending colored messages to the console (via section codes)

        // Add Plugin name as prefix (to mimic normal logging to the plugin logger)
        String plPrefix = "[" + plugin.getName() + "] ";

        if (level == Level.FINE) {
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("&7[DEBUG] " + plPrefix + message));
        } else if (level == Level.INFO) {
            // No need to add custom color for INFO messages. It will also already have the built-in prefix
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t(plPrefix + message));
        } else if (level == Level.WARNING) {
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("&e[WARNING] " + plPrefix + message));
        } else if (level == Level.SEVERE) {
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("&c[SEVERE] " + plPrefix + message));
        } else {
            // uncolored, but prefix the level
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("[" + level.getName() + "] " + plPrefix + message));
        }
    }
}
