package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import com.kamikazejam.kamicommon.nms.command.CommandMapModifier_LATEST;
import com.kamikazejam.kamicommon.nms.command.CommandMapModifier_1_8_R1;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific command map modifier implementations.
 * <p>
 * This provider selects the appropriate {@link CommandMapModifier} implementation
 * based on the current Minecraft version, handling changes in Bukkit's internal
 * command registration system. Command map modifiers enable dynamic command
 * registration and unregistration without requiring plugin reloads.
 * </p>
 * <p>
 * The provider addresses the major NMS restructuring that occurred in Minecraft 1.17:
 * <ul>
 * <li><strong>1.8-1.16:</strong> Classic NMS structure with stable command map access</li>
 * <li><strong>1.17+:</strong> Restructured NMS with updated command map handling</li>
 * </ul>
 * </p>
 * <p>
 * This allows plugins to dynamically manage commands across all supported versions,
 * enabling features like command aliasing, temporary commands, and runtime command
 * modifications without requiring server restarts.
 * </p>
 *
 * @see CommandMapModifier
 */
public class CommandMapModifierProvider extends Provider<CommandMapModifier> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the command map modifier implementation based on the major NMS
     * restructuring that occurred in Minecraft 1.17. The selection ensures
     * compatibility with both legacy and modern command registration systems.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link CommandMapModifier} implementation
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
    @Override
    protected @NotNull CommandMapModifier provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver < f("1.17")) {
            return new CommandMapModifier_1_8_R1();
        }else {
            return new CommandMapModifier_LATEST();
        }
    }
}
