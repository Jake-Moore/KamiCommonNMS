package com.kamikazejam.kamicommon.nms.command;

import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The 26.x twin of {@code CommandMapModifier_1_17_R1} in {@code versions/v1_17_R1}, and the only reason it exists is to be
 * compiled.
 * <p>
 * Nothing dispatches here. The ladder sends every server, 26.x included, to the v1_17_R1 copy, because
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
