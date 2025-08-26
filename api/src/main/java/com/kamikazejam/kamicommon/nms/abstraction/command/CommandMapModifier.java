package com.kamikazejam.kamicommon.nms.abstraction.command;

import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * NMS abstraction interface for modifying Bukkit's internal command map.
 * <p>
 * This interface provides version-specific implementations for registering and
 * unregistering commands directly with Bukkit's command system, bypassing
 * the standard plugin.yml registration process.
 * </p>
 * <p>
 * This is particularly useful for dynamic command registration at runtime
 * or for plugins that need fine-grained control over command management.
 * </p>
 */
@SuppressWarnings("unused")
public interface CommandMapModifier {

    /**
     * Unregisters a Bukkit {@link Command} from the internal command map.
     * <p>
     * This removes the command from the server's command registry, making it
     * no longer available for execution by players or console. The command
     * will be completely removed from the known commands map.
     * </p>
     *
     * @param command the {@link Command} to unregister from the command map
     */
    void unregisterCommand(@NotNull Command command);

    /**
     * Registers a Bukkit {@link Command} to the internal command map.
     * <p>
     * This adds the command to the server's command registry, making it
     * available for execution. The command will be associated with the
     * specified plugin for ownership tracking.
     * </p>
     *
     * @param command the {@link Command} to register to the command map
     * @param plugin the {@link Plugin} that owns this command
     */
    void registerCommand(@NotNull Command command, @NotNull Plugin plugin);

    /**
     * Retrieves the internal command map containing all known commands.
     * <p>
     * This provides direct access to Bukkit's internal command registry,
     * allowing inspection of all registered commands. The returned map
     * maps command names to their corresponding {@link Command} instances.
     * </p>
     *
     * @return a {@link Map} of command names to {@link Command} instances
     *         representing all known commands in the server
     */
    @NotNull
    Map<String, Command> getKnownCommands();
}
