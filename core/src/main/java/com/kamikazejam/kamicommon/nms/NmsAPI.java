package com.kamikazejam.kamicommon.nms;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.provider.BlockUtilProvider;
import com.kamikazejam.kamicommon.nms.provider.ChatColorProvider;
import com.kamikazejam.kamicommon.nms.provider.CommandMapModifierProvider;
import com.kamikazejam.kamicommon.nms.provider.ComponentLoggerAdapterProvider;
import com.kamikazejam.kamicommon.nms.provider.EnchantIDProvider;
import com.kamikazejam.kamicommon.nms.provider.EntityMethodsProvider;
import com.kamikazejam.kamicommon.nms.provider.ItemEditorProvider;
import com.kamikazejam.kamicommon.nms.provider.ItemTextProviderPre_1_17;
import com.kamikazejam.kamicommon.nms.provider.MainHandProvider;
import com.kamikazejam.kamicommon.nms.provider.MessageManagerProvider;
import com.kamikazejam.kamicommon.nms.provider.NmsItemProvider;
import com.kamikazejam.kamicommon.nms.provider.PacketHandlerProvider;
import com.kamikazejam.kamicommon.nms.provider.TeleportProvider;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.ComponentLoggerAdapter;
import com.kamikazejam.kamicommon.nms.wrapper.NMSWorldWrapper;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Central API facade for all NMS (Net Minecraft Server) functionality in KamiCommon.
 * <p>
 * This class serves as the primary entry point for accessing version-independent NMS
 * operations, providing a unified interface that abstracts away the complexity of
 * Minecraft's evolving internal APIs. It integrates all provider systems and wrapper
 * components to deliver consistent functionality across 13+ years of Minecraft evolution.
 * </p>
 * <p>
 * The API is built on a sophisticated provider system that automatically detects the
 * current Minecraft version and selects appropriate implementations for:
 * <ul>
 * <li>High-performance block manipulation and world operations</li>
 * <li>Advanced entity management and spawner mechanics</li>
 * <li>Interactive message systems with hover events and click actions</li>
 * <li>Direct packet manipulation for custom client-server communication</li>
 * <li>Command registration and management systems</li>
 * <li>Item manipulation and metadata editing</li>
 * <li>Cross-version teleportation without event triggers</li>
 * <li>Dual-wielding and main/off-hand item management</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Version Support:</strong> This API supports Minecraft versions 1.8 through 1.21+,
 * automatically adapting to major API changes including the 1.13 "flattening" update,
 * 1.17 NMS restructuring, and 1.20.5+ Mojang-mapped implementations.
 * </p>
 * <p>
 * <strong>Performance:</strong> All NMS operations provided by this API are optimized
 * for performance and bypass normal Bukkit overhead where appropriate, making them
 * suitable for high-frequency operations and bulk world modifications.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // High-performance block operations
 * AbstractBlockUtil blockUtil = NmsAPI.getBlockUtil();
 * blockUtil.setBlockSuperFast(block, XMaterial.STONE, PlaceType.NMS);
 * 
 * // Interactive messaging
 * AbstractMessageManager messageManager = NmsAPI.getMessageManager();
 * KMessage message = KMessageSingle.ofClickRunCommand("Click me!", "/help");
 * messageManager.processAndSend(player, message);
 * 
 * // Direct world access
 * NMSWorld nmsWorld = NmsAPI.getNMSWorld(bukkitWorld);
 * nmsWorld.refreshBlockAt(player, x, y, z);
 * 
 * // Cross-version item handling
 * ItemStack mainHand = NmsAPI.getItemInMainHand(player);
 * NmsAPI.setItemInOffHand(player, itemStack);
 * }</pre>
 * </p>
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NmsAPI {
    // ---------------------------------------------------------------------------------- //
    //                                    PROVIDERS                                       //
    // ---------------------------------------------------------------------------------- //
    
    /** Provider for chat color to AWT Color conversion across Minecraft versions. */
    @Getter private static final ChatColorProvider chatColorProvider = new ChatColorProvider();
    
    /** Provider for high-performance block manipulation utilities. */
    @Getter private static final BlockUtilProvider blockUtilProvider = new BlockUtilProvider();
    
    /** Provider for pre-1.17 item text and tooltip generation systems. */
    @Getter private static final ItemTextProviderPre_1_17 itemTextProviderPre_1_17 = new ItemTextProviderPre_1_17();
    
    /** Provider for direct NMS teleportation without event triggers. */
    @Getter private static final TeleportProvider teleportProvider = new TeleportProvider();
    
    /** Provider for dual-wielding and main/off-hand item management. */
    @Getter private static final MainHandProvider mainHandProvider = new MainHandProvider();
    
    /** Provider for enchantment identifier resolution across versions. */
    @Getter private static final EnchantIDProvider enchantIDProvider = new EnchantIDProvider();
    
    /** Provider for interactive message processing with hover and click events. */
    @Getter private static final MessageManagerProvider messageManagerProvider = new MessageManagerProvider();
    
    /** Provider for advanced ItemMeta manipulation capabilities. */
    @Getter private static final ItemEditorProvider itemEditorProvider = new ItemEditorProvider();
    
    /** Provider for NMS item methods and internationalization access. */
    @Getter private static final NmsItemProvider nmsItemProvider = new NmsItemProvider();
    
    /** Provider for entity manipulation and spawner mechanics. */
    @Getter private static final EntityMethodsProvider entityMethodsProvider = new EntityMethodsProvider();
    
    /** Provider for packet manipulation and custom client-server communication. */
    @Getter private static final PacketHandlerProvider packetHandlerProvider = new PacketHandlerProvider();
    
    /** Provider for dynamic command registration and management. */
    @Getter private static final CommandMapModifierProvider commandMapModifierProvider = new CommandMapModifierProvider();

    /** Provider for the server's component logging method. */
    @Getter private static final ComponentLoggerAdapterProvider componentLoggerAdapterProvider = new ComponentLoggerAdapterProvider();

    /** Wrappers for adventure text components. */
    @Getter private static final VersionedComponentSerializer versionedComponentSerializer = new VersionedComponentSerializer();

    // ---------------------------------------------------------------------------------- //
    //                                     WRAPPERS                                       //
    // ---------------------------------------------------------------------------------- //
    
    /** Wrapper factory for creating NMS world instances from Bukkit worlds. */
    @Getter private static final NMSWorldWrapper nmsWorldWrapper = new NMSWorldWrapper();

    // ---------------------------------------------------------------------------------- //
    //                                   API METHODS                                      //
    // ---------------------------------------------------------------------------------- //
    
    // Color Conversion Methods
    
    /**
     * Converts a Bukkit ChatColor to an AWT Color object.
     * <p>
     * This method provides cross-version conversion from Minecraft's chat color system
     * to Java's AWT Color representation. On versions 1.16.2+, this uses native color
     * methods for accurate conversion. On earlier versions, it uses approximated RGB values.
     * </p>
     *
     * @param chatColor the {@link ChatColor} to convert
     * @return the equivalent AWT {@link Color} object
     */
    public static Color getJavaColor(ChatColor chatColor) {
        return chatColorProvider.get().getColor(chatColor);
    }
    
    // Provider Access Methods
    
    /**
     * Retrieves the version-appropriate block utility implementation.
     * <p>
     * Block utilities provide high-performance block manipulation methods that
     * significantly outperform standard Bukkit operations, particularly for
     * bulk modifications like world generation or schematic pasting.
     * </p>
     *
     * @return the {@link AbstractBlockUtil} for the current Minecraft version
     */
    public static AbstractBlockUtil getBlockUtil() { return blockUtilProvider.get(); }
    
    /**
     * Retrieves the pre-1.17 item text implementation.
     * <p>
     * This method provides access to item text and tooltip generation systems
     * for Minecraft versions before 1.17. For 1.17+ versions, use the message
     * manager's integrated text components instead.
     * </p>
     *
     * @return the {@link AbstractItemTextPre_1_17} for pre-1.17 versions
     */
    public static AbstractItemTextPre_1_17 getItemText() { return itemTextProviderPre_1_17.get(); }
    
    /**
     * Retrieves the version-appropriate teleporter implementation.
     * <p>
     * Teleporters provide direct NMS-based player teleportation that bypasses
     * Bukkit events, offering improved performance for scenarios requiring
     * rapid or silent player movement.
     * </p>
     *
     * @return the {@link AbstractTeleporter} for the current Minecraft version
     */
    public static AbstractTeleporter getTeleporter() { return teleportProvider.get(); }
    
    /**
     * Retrieves the version-appropriate message manager implementation.
     * <p>
     * Message managers handle interactive message processing with support for
     * hover events, click actions, and rich text formatting across different
     * text component systems (Bungee/MD5 vs Kyori Adventure).
     * </p>
     *
     * @return the {@link AbstractMessageManager} for the current Minecraft version
     */
    public static AbstractMessageManager getMessageManager() { return messageManagerProvider.get(); }
    
    /**
     * Retrieves the version-appropriate item editor implementation.
     * <p>
     * Item editors provide advanced ItemMeta manipulation capabilities that
     * extend beyond standard Bukkit API functionality, including properties
     * like unbreakable flags and custom attributes.
     * </p>
     *
     * @return the {@link AbstractItemEditor} for the current Minecraft version
     */
    public static AbstractItemEditor getItemEditor() { return itemEditorProvider.get(); }
    
    /**
     * Retrieves the version-appropriate NMS item methods implementation.
     * <p>
     * NMS item methods provide access to internal item names, translation keys,
     * and other properties that are essential for plugins requiring deep item
     * inspection or compatibility with external systems.
     * </p>
     *
     * @return the {@link NmsItemMethods} for the current Minecraft version
     */
    public static NmsItemMethods getNmsItemMethods() { return nmsItemProvider.get(); }
    
    /**
     * Retrieves the version-appropriate entity methods implementation.
     * <p>
     * Entity methods provide advanced entity manipulation capabilities including
     * dimension handling, AI control, persistence settings, and spawner item
     * management across different Minecraft versions.
     * </p>
     *
     * @return the {@link AbstractEntityMethods} for the current Minecraft version
     */
    public static AbstractEntityMethods getEntityMethods() { return entityMethodsProvider.get(); }
    
    /**
     * Retrieves the version-appropriate packet handler implementation.
     * <p>
     * Packet handlers provide advanced packet manipulation capabilities for
     * custom client-server communication, entity effects, and network operations
     * that go beyond standard Bukkit capabilities.
     * </p>
     *
     * @return the {@link NMSPacketHandler} for the current Minecraft version
     */
    public static NMSPacketHandler getPacketHandler() { return packetHandlerProvider.get(); }

    /**
     * Retrieves the version-appropriate command map modifier implementation.
     * <p>
     * Command map modifiers enable dynamic command registration and unregistration
     * without requiring plugin reloads, supporting features like command aliasing
     * and runtime command modifications.
     * </p>
     *
     * @return the {@link CommandMapModifier} for the current Minecraft version
     */
    public static CommandMapModifier getCommandMapModifier() { return commandMapModifierProvider.get(); }

    /**
     * Retrieves the version-appropriate logger adapter that supports component logging.
     *
     * @return the {@link ComponentLoggerAdapter} for the current Minecraft version
     */
    public static ComponentLoggerAdapter getComponentLoggerAdapter() { return componentLoggerAdapterProvider.get(); }

    // Hand Management Methods
    
    /**
     * Retrieves the item in the player's main hand.
     * <p>
     * This method provides cross-version access to main hand items, properly
     * handling the transition from single-hand (1.8) to dual-wielding (1.9+) systems.
     * </p>
     *
     * @param player the {@link Player} to query
     * @return the {@link ItemStack} in the main hand, or {@code null} if empty
     */
    public static @Nullable ItemStack getItemInMainHand(@NotNull Player player) {
        return mainHandProvider.get().getItemInMainHand(player);
    }
    
    /**
     * Sets the item in the player's main hand.
     * <p>
     * This method provides cross-version main hand item setting, ensuring
     * compatibility across both single-hand and dual-wielding versions.
     * </p>
     *
     * @param player the {@link Player} to modify
     * @param itemStack the {@link ItemStack} to place in the main hand, or {@code null} to clear
     */
    public static void setItemInMainHand(@NotNull Player player, @Nullable ItemStack itemStack) {
        mainHandProvider.get().setItemInMainHand(player, itemStack);
    }
    
    /**
     * Retrieves the item in the player's off-hand.
     * <p>
     * This method provides cross-version access to off-hand items. On versions
     * before 1.9 (which lack dual-wielding), this always returns {@code null}.
     * </p>
     *
     * @param player the {@link Player} to query
     * @return the {@link ItemStack} in the off-hand, or {@code null} if empty or unsupported
     */
    public static @Nullable ItemStack getItemInOffHand(@NotNull Player player) {
        return mainHandProvider.get().getItemInOffHand(player);
    }
    
    /**
     * Sets the item in the player's off-hand.
     * <p>
     * This method provides cross-version off-hand item setting. On versions
     * before 1.9 (which lack dual-wielding), this throws an {@link UnsupportedOperationException}.
     * </p>
     *
     * @param player the {@link Player} to modify
     * @param itemStack the {@link ItemStack} to place in the off-hand, or {@code null} to clear
     * @throws UnsupportedOperationException if called on versions before 1.9
     */
    public static void setItemInOffHand(@NotNull Player player, @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        mainHandProvider.get().setItemInOffHand(player, itemStack);
    }

    // Enchantment Methods
    
    /**
     * Retrieves the namespaced identifier for an enchantment.
     * <p>
     * This method provides cross-version access to enchantment identifiers,
     * handling the transition from numeric IDs (pre-1.13) to namespaced
     * identifiers (1.13+) introduced by the "flattening" update.
     * </p>
     *
     * @param enchantment the {@link Enchantment} to identify
     * @return the namespaced identifier string (e.g., "minecraft:sharpness")
     */
    public static String getNamespaced(Enchantment enchantment) {
        return enchantIDProvider.get().getNamespaced(enchantment);
    }
    
    // World Wrapper Methods
    
    /**
     * Creates an NMS world wrapper for the specified Bukkit world.
     * <p>
     * This method creates a version-appropriate NMS world wrapper that provides
     * direct access to Minecraft's internal world representation, enabling
     * high-performance world operations, chunk management, and entity spawning.
     * </p>
     *
     * @param world the Bukkit {@link World} to wrap
     * @return an {@link NMSWorld} wrapper for the specified world
     */
    public static @NotNull NMSWorld getNMSWorld(@NotNull World world) {
        return nmsWorldWrapper.get(world);
    }
}
