package com.kamikazejam.kamicommon.nms.bundle.v_latest;

import org.jetbrains.annotations.ApiStatus;
import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.block.BlockUtil_LATEST;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.chat.MessageManager_LATEST;
import com.kamikazejam.kamicommon.nms.command.CommandMapModifier_LATEST;
import com.kamikazejam.kamicommon.nms.entity.EntityMethods_LATEST;
import com.kamikazejam.kamicommon.nms.item.NmsItemMethods_LATEST;
import com.kamikazejam.kamicommon.nms.provider.Provider;
import com.kamikazejam.kamicommon.nms.teleport.Teleporter_LATEST;
import com.kamikazejam.kamicommon.nms.text.TextPlaceholder;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_LATEST;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler_LATEST;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld_LATEST;
import java.util.List;
import org.bukkit.World;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@code v_latest} module's implementation of {@link NmsBundle}.
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
        return new BlockUtil_LATEST();
    }

    @Override
    public @NotNull AbstractEntityMethods entityMethods() {
        return new EntityMethods_LATEST();
    }

    @Override
    public @NotNull NmsItemMethods nmsItemMethods() {
        return new NmsItemMethods_LATEST();
    }

    @Override
    public @NotNull NMSWorld nmsWorld(@NotNull World world, @NotNull Provider<AbstractBlockUtil> blockUtil) {
        return new NMSWorld_LATEST(world, blockUtil);
    }

    @Override
    public @NotNull NMSPacketHandler packetHandler() {
        return new NMSPacketHandler_LATEST();
    }

    @Override
    public @NotNull AbstractTeleporter teleporter() {
        return new Teleporter_LATEST();
    }

    @Override
    public @NotNull CommandMapModifier commandMapModifier() {
        return new CommandMapModifier_LATEST();
    }

    @Override
    public @NotNull AbstractMessageManager messageManager() {
        return new MessageManager_LATEST();
    }

    // The native-Adventure component implementation, the same one v1_21_4 exposes, compiled against
    // Paper 26.x rather than 1.21.4. It needs net.kyori.adventure.text.serializer.json and
    // ItemMeta.customName(), both of which 26.x still carries.

    @Override
    public @NotNull VersionedComponent componentFromJson(@NotNull String json) {
        return VersionedComponent_LATEST.fromJson(json);
    }

    @Override
    public @NotNull VersionedComponent componentFromPlainText(@NotNull String text) {
        return VersionedComponent_LATEST.fromPlainText(text);
    }

    @Override
    public @NotNull VersionedComponent componentFromMiniMessage(@NotNull String miniMessage) {
        return VersionedComponent_LATEST.fromMiniMessage(miniMessage);
    }

    @Override
    public @NotNull VersionedComponent componentFromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders) {
        return VersionedComponent_LATEST.fromMiniMessage(miniMessage, placeholders);
    }

    @Override
    public @NotNull VersionedComponent componentFromLegacyAmpersand(@NotNull String legacy) {
        return VersionedComponent_LATEST.fromLegacyAmpersand(legacy);
    }

    @Override
    public @NotNull VersionedComponent componentFromLegacySection(@NotNull String legacy) {
        return VersionedComponent_LATEST.fromLegacySection(legacy);
    }

    @Override
    public @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent component) {
        return VersionedComponent_LATEST.setDisplayName(meta, component);
    }

    @Override
    public @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        return VersionedComponent_LATEST.getDisplayName(meta);
    }

    @Override
    public @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        return VersionedComponent_LATEST.setLore(meta, lore);
    }

    @Override
    public @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta) {
        return VersionedComponent_LATEST.getLore(meta);
    }

    @Override
    public @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent component) {
        return VersionedComponent_LATEST.addLoreLine(meta, component);
    }
}
