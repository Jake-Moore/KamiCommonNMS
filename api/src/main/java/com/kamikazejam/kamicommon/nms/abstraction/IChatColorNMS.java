package com.kamikazejam.kamicommon.nms.abstraction;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * NMS abstraction interface for converting Bukkit {@link ChatColor} instances 
 * to AWT {@link Color} objects.
 * <p>
 * This interface provides version-specific implementations to handle the conversion
 * between Minecraft's chat color system and Java's color representation.
 * </p>
 */
public interface IChatColorNMS {

    /**
     * Converts a Bukkit {@link ChatColor} to an AWT {@link Color} object.
     * <p>
     * This method attempts to map the given chat color to its corresponding
     * RGB color value. Not all chat colors have direct color representations
     * (such as formatting codes like BOLD, ITALIC, etc.).
     * </p>
     *
     * @param chatColor the {@link ChatColor} to convert
     * @return the corresponding {@link Color} object, or {@code null} if the
     *         chat color cannot be converted to a color (e.g., formatting codes)
     */
    @Nullable Color getColor(ChatColor chatColor);
}
