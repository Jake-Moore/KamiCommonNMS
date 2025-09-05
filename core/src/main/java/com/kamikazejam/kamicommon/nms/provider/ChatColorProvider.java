package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.IChatColorNMS;
import com.kamikazejam.kamicommon.nms.chatcolor.ChatColor_LATEST;
import com.kamikazejam.kamicommon.nms.chatcolor.ChatColor1_8_R3;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific chat color implementations.
 * <p>
 * This provider selects the appropriate {@link IChatColorNMS} implementation
 * based on the current Minecraft version, handling the evolution of chat color
 * APIs and the introduction of hex color support in Minecraft 1.16.2+.
 * </p>
 * <p>
 * The provider addresses a significant API change in Minecraft's color handling:
 * <ul>
 * <li><strong>Pre-1.16.2:</strong> Limited to 16 predefined chat colors with approximated AWT colors</li>
 * <li><strong>1.16.2+:</strong> Native hex color support with direct color retrieval methods</li>
 * </ul>
 * </p>
 * <p>
 * This allows plugins to consistently convert Bukkit {@link org.bukkit.ChatColor}
 * values to AWT {@link java.awt.Color} objects across all supported Minecraft versions,
 * with enhanced accuracy on newer versions that support hex colors.
 * </p>
 *
 * @see IChatColorNMS
 */
public class ChatColorProvider extends Provider<IChatColorNMS> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the chat color implementation based on hex color support availability.
     * Versions 1.16.2+ can use native color methods, while older versions rely
     * on color approximations for the standard 16 chat colors.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link IChatColorNMS} implementation
     */
    @Override
    protected @NotNull IChatColorNMS provide(int ver) {
        // If we are 1.16.2+ we can use the getColor() method introduced when hex codes were added
        if (ver >= 1162) {
            return new ChatColor_LATEST();
        }
        // Otherwise we have to approximate
        return new ChatColor1_8_R3();
    }
}
