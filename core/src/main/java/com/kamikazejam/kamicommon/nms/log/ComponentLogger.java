package com.kamikazejam.kamicommon.nms.log;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.log.LoggerService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * A simple logger service for a specific plugin, providing additional support for sending components to the console.
 */
@SuppressWarnings("unused")
@Getter
public class ComponentLogger extends LoggerService {
    private final @NotNull Plugin plugin;
    @Setter
    private boolean debug = false;
    @Setter
    private boolean translateLegacyAmpersandColors  = true;
    @Setter
    private boolean parseLegacySectionColors = true;
    public ComponentLogger(@NotNull Plugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin cannot be null");
        this.plugin = plugin;
    }

    @Override
    public String getLoggerName() {
        return plugin.getName();
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    // ------------------------------------------------------------ //
    //    String Method Inherited from LoggerService (uncolored)    //
    // ------------------------------------------------------------ //
    @Override
    public void logToConsole(@NotNull String message, @NotNull Level level) {
        Preconditions.checkNotNull(message, "message cannot be null");
        Preconditions.checkNotNull(level, "level cannot be null");
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();

        // use plain text if color parsing is disabled
        if (!this.isParseLegacySectionColors()) {
            this.logToConsole(serializer.fromPlainText(message), level);
            return;
        }

        // color parsing is enabled, but if ampersand translation is disabled
        if (!this.isTranslateLegacyAmpersandColors()) {
            // translate only section symbols
            this.logToConsole(serializer.fromLegacySection(message), level);
            return;
        }

        // color parsing is enabled, and ampersand translation is enabled
        // translate ampersands to section symbols first, then parse
        this.logToConsole(serializer.fromLegacySection(LegacyColors.t(message)), level);
    }

    // ------------------------------------------------------------ //
    //   Component Methods mirrored, but using VersionedComponent   //
    // ------------------------------------------------------------ //
    public void info(@NotNull VersionedComponent message) {
        logToConsole(message, Level.INFO);
    }

    public void info(@NotNull Throwable throwable) {
        logToConsole(throwable.getMessage(), Level.INFO);
        throwable.printStackTrace();
    }

    public void info(@NotNull Throwable throwable, @NotNull VersionedComponent message) {
        logToConsole(message + " - " + throwable.getMessage(), Level.INFO);
        throwable.printStackTrace();
    }

    public void debug(@NotNull VersionedComponent message) {
        if (!isDebug()) {
            return;
        }
        logToConsole(message, Level.FINE);
    }

    public void warn(@NotNull VersionedComponent message) {
        logToConsole(message, Level.WARNING);
    }

    public void warn(@NotNull Throwable throwable) {
        logToConsole(throwable.getMessage(), Level.WARNING);
        throwable.printStackTrace();
    }

    public void warn(@NotNull Throwable throwable, @NotNull VersionedComponent message) {
        logToConsole(message + " - " + throwable.getMessage(), Level.WARNING);
        throwable.printStackTrace();
    }

    public void warning(@NotNull VersionedComponent message) {
        this.warn(message);
    }

    public void warning(@NotNull Throwable throwable) {
        this.warn(throwable);
    }

    public void warning(@NotNull Throwable throwable, @NotNull VersionedComponent message) {
        this.warn(throwable, message);
    }

    public void severe(@NotNull VersionedComponent message) {
        logToConsole(message, Level.SEVERE);
    }

    public void severe(@NotNull Throwable throwable) {
        logToConsole(throwable.getMessage(), Level.SEVERE);
        throwable.printStackTrace();
    }

    public void severe(@NotNull Throwable throwable, @NotNull VersionedComponent message) {
        logToConsole(message + " - " + throwable.getMessage(), Level.SEVERE);
        throwable.printStackTrace();
    }

    public void error(@NotNull VersionedComponent message) {
        this.severe(message);
    }

    public void error(@NotNull Throwable throwable) {
        this.severe(throwable);
    }

    public void error(@NotNull Throwable throwable, @NotNull VersionedComponent message) {
        this.severe(throwable, message);
    }

    public void logToConsole(@NotNull VersionedComponent message, @NotNull Level level) {
        Preconditions.checkNotNull(message, "message cannot be null");
        Preconditions.checkNotNull(level, "level cannot be null");

        // Use the NMS logger adapter to send the component to the console
        NmsAPI.getComponentLoggerAdapter().log(this.plugin, message, level);
    }
}
