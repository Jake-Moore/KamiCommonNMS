package com.kamikazejam.kamicommon.nms.bundle;

import com.kamikazejam.kamicommon.nms.abstraction.IChatColorNMS;
import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import com.kamikazejam.kamicommon.nms.abstraction.enchantid.AbstractEnchantID;
import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import com.kamikazejam.kamicommon.nms.abstraction.mainhand.AbstractMainHand;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.library.worldedit.WorldEditApi;
import com.kamikazejam.kamicommon.nms.library.worldguard.WorldGuardApi;
import com.kamikazejam.kamicommon.nms.provider.Provider;
import com.kamikazejam.kamicommon.nms.text.ComponentLoggerAdapter;
import com.kamikazejam.kamicommon.nms.text.TextPlaceholder;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.wrappers.NMSWrapper;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The single entry point into one {@code versions/*} module.
 * <p>
 * Every implementation of this interface lives <i>inside</i> the version module it speaks for, is
 * named {@code com.kamikazejam.kamicommon.nms.bundle.<module>.NmsBundleImpl}, and is loaded by
 * {@link NmsBundles#forModule(String)} through {@link Class#forName(String)}, never named
 * statically from {@code :core}.
 * </p>
 * <p>
 * <b>That indirection is the whole point, and it is not stylistic.</b> Every
 * {@code Provider.provide(int)} in this library is declared to return an abstract type and then
 * constructs a subclass. That forces the verifier to perform an assignability check, and the check
 * loads the subclass.
 * So a {@code new Teleporter_LATEST()} sitting in a branch that a 1.8.8 server never takes still
 * stops the whole provider from loading there, with {@link UnsupportedClassVersionError}, before any
 * method runs. Resolving the module by name defers that load until the branch is actually taken, so
 * each {@code versions/*} module can target the JVM its own Minecraft version required.
 * </p>
 * <p>
 * Every capability defaults to throwing. A module implements only what it ships: {@code v1_21_9}
 * carries one class and overrides one method, while {@code v_latest} overrides most of them. Adding
 * a capability here is therefore source-compatible with every existing module.
 * </p>
 *
 * @see NmsBundles
 */
@SuppressWarnings("unused")
public interface NmsBundle {

    // ----------------------------------------------------------------------------------------- //
    //                                    Version-specific NMS                                     //
    // ----------------------------------------------------------------------------------------- //

    default @NotNull AbstractTeleporter teleporter() { throw missing("teleporter"); }

    default @NotNull AbstractBlockUtil blockUtil() { throw missing("blockUtil"); }

    default @NotNull IChatColorNMS chatColor() { throw missing("chatColor"); }

    default @NotNull CommandMapModifier commandMapModifier() { throw missing("commandMapModifier"); }

    default @NotNull ComponentLoggerAdapter componentLoggerAdapter() { throw missing("componentLoggerAdapter"); }

    default @NotNull AbstractEnchantID enchantId() { throw missing("enchantId"); }

    default @NotNull AbstractEntityMethods entityMethods() { throw missing("entityMethods"); }

    default @NotNull AbstractItemEditor itemEditor() { throw missing("itemEditor"); }

    default @NotNull AbstractItemTextPre_1_17 itemText() { throw missing("itemText"); }

    default @NotNull AbstractMainHand mainHand() { throw missing("mainHand"); }

    default @NotNull AbstractMessageManager messageManager() { throw missing("messageManager"); }

    /**
     * The pre-1.17 form. Those implementations render through md5's {@code BaseComponent} and need
     * the version's own item-text renderer handed to them; 1.17+ uses adventure and takes nothing.
     *
     * @param itemText the item-text implementation for this server version
     * @return this module's message manager
     */
    default @NotNull AbstractMessageManager messageManager(@NotNull AbstractItemTextPre_1_17 itemText) {
        throw missing("messageManager(AbstractItemTextPre_1_17)");
    }

    default @NotNull NmsItemMethods nmsItemMethods() { throw missing("nmsItemMethods"); }

    default @NotNull NMSPacketHandler packetHandler() { throw missing("packetHandler"); }

    default @NotNull Listener preSpawnSpawnerAdapter() { throw missing("preSpawnSpawnerAdapter"); }

    default @NotNull NMSWorld nmsWorld(@NotNull World world, @NotNull Provider<AbstractBlockUtil> blockUtil) {
        throw missing("nmsWorld");
    }

    // ----------------------------------------------------------------------------------------- //
    //                                    WorldEdit / WorldGuard                                   //
    // ----------------------------------------------------------------------------------------- //
    // Dispatched on the WorldEdit *plugin* version rather than the Minecraft version, but routed
    // through here for the same reason as everything else: worldguard-bukkit 7.0.9 is entirely
    // class-file major 61, so naming WorldGuard7 statically breaks a Java 8 server whether or not
    // WorldGuard is installed on it.

    default @NotNull WorldEditApi<?> worldEdit() { throw missing("worldEdit"); }

    default @NotNull WorldGuardApi worldGuard(@NotNull Plugin worldGuardPlugin,
                                              @NotNull NMSWrapper<NMSWorld, World> worldWrapper) {
        throw missing("worldGuard");
    }

    // ----------------------------------------------------------------------------------------- //
    //                                    Versioned components                                     //
    // ----------------------------------------------------------------------------------------- //
    // Two implementations, split at 1.21.4. Below that the shaded Adventure is used, because the
    // native one needs ItemMeta.customName(), which Paper only added in 1.21.4. Both paths are kept
    // because they are not equivalent. The native serializers are the reason there is a boundary
    // here at all.

    // componentFrom(Component) deliberately does NOT live here. A shaded-typed method declared on
    // this interface is harmless in a descriptor, but each module's NmsBundleImpl OVERRODE it, and
    // verifying an override loads the parameter type. That made Class.forName on any adapter resolve
    // the shaded Adventure copy. It now lives on ShadedComponentBridge, loaded on demand.

    default @NotNull VersionedComponent componentFromPlainText(@NotNull String text) { throw missing("componentFromPlainText"); }

    default @NotNull VersionedComponent componentFromMiniMessage(@NotNull String miniMessage) { throw missing("componentFromMiniMessage"); }

    /**
     * MiniMessage with tag replacements, without the caller naming an Adventure {@code TagResolver}.
     * <p>
     * Each {@link TextPlaceholder} is converted to whichever resolver this module's Adventure copy
     * uses. That conversion is the whole reason this overload exists: it keeps the resolver type
     * inside the {@code versions/*} module, where the shaded Adventure belongs.
     * </p>
     */
    default @NotNull VersionedComponent componentFromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders) { throw missing("componentFromMiniMessage(String, TextPlaceholder...)"); }

    default @NotNull VersionedComponent componentFromLegacyAmpersand(@NotNull String legacy) { throw missing("componentFromLegacyAmpersand"); }

    default @NotNull VersionedComponent componentFromLegacySection(@NotNull String legacy) { throw missing("componentFromLegacySection"); }

    default @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent component) { throw missing("setDisplayName"); }

    default @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) { throw missing("getDisplayName"); }

    default @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) { throw missing("setLore"); }

    default @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta) { throw missing("getLore"); }

    default @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent component) { throw missing("addLoreLine"); }

    // ----------------------------------------------------------------------------------------- //

    /**
     * The exception every unimplemented capability throws. Names the adapter, which names the
     * module, so a dispatch table pointing at the wrong module says so instead of returning null.
     *
     * @param capability the method that was called
     * @return the exception to throw
     */
    default @NotNull UnsupportedOperationException missing(@NotNull String capability) {
        return new UnsupportedOperationException(
                getClass().getName() + " provides no " + capability + "() implementation");
    }
}
