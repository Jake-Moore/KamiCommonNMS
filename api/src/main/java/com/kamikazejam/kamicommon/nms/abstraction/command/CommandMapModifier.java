package com.kamikazejam.kamicommon.nms.abstraction.command;

import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("unused")
public interface CommandMapModifier {
    /**
     * Unregister a given Bukkit {@link Command} from the internal command map.
     * @param command the command to unregister
     */
    void unregisterCommand(@NotNull Command command);

    /**
     * Register a given Bukkit {@link Command} to the internal command map.
     * @param command the command to register
     * @param plugin the plugin registering the command
     */
    void registerCommand(@NotNull Command command, @NotNull Plugin plugin);

    /**
     * Fetch the internal command map.
     */
    @NotNull
    Map<String, Command> getKnownCommands();
}
