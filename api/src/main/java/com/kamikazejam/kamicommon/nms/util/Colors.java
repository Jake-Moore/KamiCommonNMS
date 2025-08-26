package com.kamikazejam.kamicommon.nms.util;

import lombok.Getter;

import java.awt.*;

/**
 * Enumeration of Minecraft chat colors with their corresponding AWT Color representations.
 * <p>
 * This enum provides a mapping between Minecraft's chat color system and Java's
 * {@link Color} class, enabling consistent color representation across different
 * contexts such as chat formatting, GUI elements, and graphical operations.
 * </p>
 * <p>
 * Each color entry contains:
 * <ul>
 * <li>A character code used in Minecraft's chat color formatting (§0-§f)</li>
 * <li>A string name matching Minecraft's internal color naming</li>
 * <li>An AWT {@link Color} object with the equivalent RGB values</li>
 * </ul>
 * </p>
 * <p>
 * The color values are based on Minecraft's default chat color palette,
 * ensuring visual consistency between in-game text and external applications
 * that need to display Minecraft-styled content.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Get the AWT Color for red text
 * Color redColor = Colors.RED.getColor();
 * 
 * // Get the chat color code
 * char redCode = Colors.RED.getCode(); // 'c'
 * 
 * // Format chat message
 * String message = "§" + Colors.RED.getCode() + "This is red text";
 * }</pre>
 * </p>
 */
@Getter
public enum Colors {
    /** Black color (§0) - RGB: 0x000000 */
    BLACK('0', "black", new Color(0x000000)),
    
    /** Dark Blue color (§1) - RGB: 0x0000AA */
    DARK_BLUE('1', "dark_blue", new Color(0x0000AA)),
    
    /** Dark Green color (§2) - RGB: 0x00AA00 */
    DARK_GREEN('2', "dark_green", new Color(0x00AA00)),
    
    /** Dark Aqua color (§3) - RGB: 0x00AAAA */
    DARK_AQUA('3', "dark_aqua", new Color(0x00AAAA)),
    
    /** Dark Red color (§4) - RGB: 0xAA0000 */
    DARK_RED('4', "dark_red", new Color(0xAA0000)),
    
    /** Dark Purple color (§5) - RGB: 0xAA00AA */
    DARK_PURPLE('5', "dark_purple", new Color(0xAA00AA)),
    
    /** Gold color (§6) - RGB: 0xFFAA00 */
    GOLD('6', "gold", new Color(0xFFAA00)),
    
    /** Gray color (§7) - RGB: 0xAAAAAA */
    GRAY('7', "gray", new Color(0xAAAAAA)),
    
    /** Dark Gray color (§8) - RGB: 0x555555 */
    DARK_GRAY('8', "dark_gray", new Color(0x555555)),
    
    /** Blue color (§9) - RGB: 0x5555FF */
    BLUE('9', "blue", new Color(0x5555FF)),
    
    /** Green color (§a) - RGB: 0x55FF55 */
    GREEN('a', "green", new Color(0x55FF55)),
    
    /** Aqua color (§b) - RGB: 0x55FFFF */
    AQUA('b', "aqua", new Color(0x55FFFF)),
    
    /** Red color (§c) - RGB: 0xFF5555 */
    RED('c', "red", new Color(0xFF5555)),
    
    /** Light Purple color (§d) - RGB: 0xFF55FF */
    LIGHT_PURPLE('d', "light_purple", new Color(0xFF55FF)),
    
    /** Yellow color (§e) - RGB: 0xFFFF55 */
    YELLOW('e', "yellow", new Color(0xFFFF55)),
    
    /** White color (§f) - RGB: 0xFFFFFF */
    WHITE('f', "white", new Color(0xFFFFFF)),
    ;

    /**
     * The character code used in Minecraft chat color formatting.
     * <p>
     * This character follows the section symbol (§) to create colored text
     * in Minecraft. For example, "§c" produces red text.
     * </p>
     */
    private final char code;
    
    /**
     * The string name of the color as used in Minecraft's internal systems.
     * <p>
     * This name matches Minecraft's internal color naming conventions
     * and can be used for serialization or lookup purposes.
     * </p>
     */
    private final String name;
    
    /**
     * The AWT Color representation with equivalent RGB values.
     * <p>
     * This {@link Color} object contains the RGB values that visually
     * match the corresponding Minecraft chat color, enabling consistent
     * color representation in Java applications.
     * </p>
     */
    private final Color color;

    /**
     * Creates a new color enum entry with the specified properties.
     *
     * @param code the character code used for chat formatting
     * @param name the string name of the color
     * @param color the AWT {@link Color} representation
     */
    Colors(char code, String name, Color color) {
        this.code = code;
        this.name = name;
        this.color = color;
    }
}
