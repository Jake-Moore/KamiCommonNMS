package com.kamikazejam.kamicommon.nms.command;

import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This class just needs the Bukkit.getCommandMap().getKnownCommands() methods to be available.<br>
 * In our current repo structure, this requires paper (for getCommandMap()) and the first paper module is 1.17,<br>
 *  which also haas the getKnownCommands() method. Prior to 1.17 the logic in the 1_8_R1 version of this class is fine
 * <br>
 * <br>
 * Working for: 1.17+, 1.21.8
 */
public class CommandMapModifier_LATEST implements CommandMapModifier {
    private final CommandMap commandMap;
    public CommandMapModifier_LATEST() {
        commandMap = Bukkit.getCommandMap();
    }

    @Override
    public void unregisterCommand(@NotNull Command command) {
        command.unregister(commandMap);
    }

    @Override
    public void registerCommand(@NotNull Command command, @NotNull Plugin plugin) {
        commandMap.register(plugin.getName(), command);
    }

    @Override
    public @NotNull Map<String, Command> getKnownCommands() {
        return commandMap.getKnownCommands();
    }
}
