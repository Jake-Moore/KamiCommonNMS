package com.kamikazejam.kamicommon.nms.bundle.v1_17_R1;

import org.jetbrains.annotations.ApiStatus;
import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemNbt;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.block.BlockUtil1_17_R1;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.chat.MessageManager_1_17_R1;
import com.kamikazejam.kamicommon.nms.command.CommandMapModifier_1_17_R1;
import com.kamikazejam.kamicommon.nms.entity.EntityMethods_1_17_R1;
import com.kamikazejam.kamicommon.nms.item.ItemNbt_1_17_R1;
import com.kamikazejam.kamicommon.nms.item.NmsItemMethods_1_17_R1;
import com.kamikazejam.kamicommon.nms.provider.Provider;
import com.kamikazejam.kamicommon.nms.teleport.Teleporter1_17_R1;
import com.kamikazejam.kamicommon.nms.text.TextPlaceholder;
import com.kamikazejam.kamicommon.nms.text.TextBundles;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler_1_17_R1;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld_1_17_R1;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The {@code v1_17_R1} module's implementation of {@link NmsBundle}.
 * <p>
 * Loaded by name from {@link com.kamikazejam.kamicommon.nms.bundle.NmsBundles}, never referenced
 * statically from {@code :core}. Everything below is an ordinary compile-time call, so the module's
 * own contents stay fully type-checked; only the boundary into the module is reflective.
 * </p>
 * <p>
 * <b>Generated shape, maintained by hand.</b> Adding a class to this module means adding its
 * accessor here, or {@code :core} cannot reach it.
 * </p>
 */
@ApiStatus.Internal
public class NmsBundleImpl implements NmsBundle {

    @Override
    public @NotNull AbstractBlockUtil blockUtil() {
        return new BlockUtil1_17_R1();
    }

    @Override
    public @NotNull AbstractEntityMethods entityMethods() {
        return new EntityMethods_1_17_R1();
    }

    @Override
    public @NotNull NmsItemMethods nmsItemMethods() {
        return new NmsItemMethods_1_17_R1();
    }

    @Override
    public @NotNull AbstractItemNbt itemNbt() {
        return new ItemNbt_1_17_R1();
    }

    @Override
    public @NotNull NMSWorld nmsWorld(@NotNull World world, @NotNull Provider<AbstractBlockUtil> blockUtil) {
        return new NMSWorld_1_17_R1(world, blockUtil);
    }

    @Override
    public @NotNull NMSPacketHandler packetHandler() {
        return new NMSPacketHandler_1_17_R1();
    }

    @Override
    public @NotNull AbstractTeleporter teleporter() {
        return new Teleporter1_17_R1();
    }

    @Override
    public @NotNull CommandMapModifier commandMapModifier() {
        return new CommandMapModifier_1_17_R1();
    }

    @Override
    public @NotNull AbstractMessageManager messageManager() {
        return new MessageManager_1_17_R1();
    }

    // 1.17 and 1.18.1 have Adventure on the server but not MiniMessage, so components are still
    // built from the shaded copy. This lives here rather than in v1_18_R1 because 1.17 runs on
    // Java 16 and that module targets 17.

    @Override
    public @NotNull VersionedComponent componentFromJson(@NotNull String json) {
        return TextBundles.forModule("v1_17_R1").fromJson(json);
    }

    @Override
    public @NotNull VersionedComponent componentFromPlainText(@NotNull String text) {
        return TextBundles.forModule("v1_17_R1").fromPlainText(text);
    }

    @Override
    public @NotNull VersionedComponent componentFromMiniMessage(@NotNull String miniMessage) {
        return TextBundles.forModule("v1_17_R1").fromMiniMessage(miniMessage);
    }

    @Override
    public @NotNull VersionedComponent componentFromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders) {
        return TextBundles.forModule("v1_17_R1").fromMiniMessage(miniMessage, placeholders);
    }

    @Override
    public @NotNull VersionedComponent componentFromLegacyAmpersand(@NotNull String legacy) {
        return TextBundles.forModule("v1_17_R1").fromLegacyAmpersand(legacy);
    }

    @Override
    public @NotNull VersionedComponent componentFromLegacySection(@NotNull String legacy) {
        return TextBundles.forModule("v1_17_R1").fromLegacySection(legacy);
    }

    @Override
    public @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent component) {
        return TextBundles.forModule("v1_17_R1").setDisplayName(meta, component);
    }

    @Override
    public @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        return TextBundles.forModule("v1_17_R1").getDisplayName(meta);
    }

    @Override
    public @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        return TextBundles.forModule("v1_17_R1").setLore(meta, lore);
    }

    @Override
    public @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta) {
        return TextBundles.forModule("v1_17_R1").getLore(meta);
    }

    @Override
    public @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent component) {
        return TextBundles.forModule("v1_17_R1").addLoreLine(meta, component);
    }

}
