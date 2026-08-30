package com.kamikazejam.kamicommon.nms.text;

import org.jetbrains.annotations.ApiStatus;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.kamikazejam.kamicommon.util.LegacyColors;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Console logging for servers below 1.17, which is to say servers running on Java 8.
 * <p>
 * A copy of {@code ComponentLoggerAdapter_1_18_R1}, and the duplication is deliberate. The code is
 * identical because the behaviour is: neither version has Paper's Adventure {@code ComponentLogger},
 * so both write section-coded text to the console sender. What differs is the JVM. {@code v1_18_R1}
 * targets Java 17 because 1.17 and 1.18 required it, so routing a 1.8.8 server there to reach the
 * same code loads a Java 17 class on a Java 8 JVM and the plugin fails to enable.
 * </p>
 * <p>
 * This is the same move the library already makes at every NMS boundary, which is to fork the module
 * and keep the naming. Here it applies to a JVM boundary instead of an API one.
 * </p>
 */
@ApiStatus.Internal
public class ComponentLoggerAdapter_1_16_R3 implements ComponentLoggerAdapter {
    public void log(@NotNull Plugin plugin, @NotNull VersionedComponent message, @NotNull Level level) {
        // This is an implementation for versions before paper added adventure support
        // It uses the old (backwards compatible) way of sending colored messages to the console (via section codes)

        // Add Plugin name as prefix (to mimic normal logging to the plugin logger)
        String plPrefix = "[" + plugin.getName() + "] ";

        // Serialize rather than concatenate. No VersionedComponent implementation overrides
        // toString(), so "prefix + message" printed an object identity --
        // com.kamikazejam.kamicommon.nms.text.VersionedComponent_1_11_R1@3466377f, instead of the
        // text. That has always been true of this code path; it went unnoticed because until the
        // Java floor dropped, no server old enough to reach it could load KamiCommon at all.
        String text = LegacyComponentSerializer.legacySection().serialize(ShadedBacked.of(message));

        if (level == Level.FINE) {
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("&7[DEBUG] " + plPrefix + text));
        } else if (level == Level.INFO) {
            // No need to add custom color for INFO messages. It will also already have the built-in prefix
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t(plPrefix + text));
        } else if (level == Level.WARNING) {
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("&e[WARNING] " + plPrefix + text));
        } else if (level == Level.SEVERE) {
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("&c[SEVERE] " + plPrefix + text));
        } else {
            // uncolored, but prefix the level
            Bukkit.getConsoleSender().sendMessage(LegacyColors.t("[" + level.getName() + "] " + plPrefix + text));
        }
    }
}
