package com.kamikazejam.kamicommon.nms.bundle.v1_18_R2;

import org.jetbrains.annotations.ApiStatus;
import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.block.BlockUtil1_18_R2;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.entity.EntityMethods_1_18_R2;
import com.kamikazejam.kamicommon.nms.item.NmsItemMethods_1_18_R2;
import com.kamikazejam.kamicommon.nms.provider.Provider;
import com.kamikazejam.kamicommon.nms.teleport.Teleporter1_18_R2;
import com.kamikazejam.kamicommon.nms.text.ComponentLoggerAdapter;
import com.kamikazejam.kamicommon.nms.text.ComponentLoggerAdapter_1_18_R2;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler_1_18_R2;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld_1_18_R2;
import org.bukkit.World;
import com.kamikazejam.kamicommon.nms.text.TextPlaceholder;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_1_18_R2;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code v1_18_R2} module's implementation of {@link NmsBundle}.
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
        return new BlockUtil1_18_R2();
    }

    @Override
    public @NotNull NmsItemMethods nmsItemMethods() {
        return new NmsItemMethods_1_18_R2();
    }

    @Override
    public @NotNull NMSWorld nmsWorld(@NotNull World world, @NotNull Provider<AbstractBlockUtil> blockUtil) {
        return new NMSWorld_1_18_R2(world, blockUtil);
    }

    @Override
    public @NotNull NMSPacketHandler packetHandler() {
        return new NMSPacketHandler_1_18_R2();
    }

    @Override
    public @NotNull AbstractTeleporter teleporter() {
        return new Teleporter1_18_R2();
    }

    @Override
    public @NotNull ComponentLoggerAdapter componentLoggerAdapter() {
        return new ComponentLoggerAdapter_1_18_R2();
    }

    @Override
    public @NotNull AbstractEntityMethods entityMethods() {
        return new EntityMethods_1_18_R2();
    }

    @Override
    public @NotNull VersionedComponent componentFromJson(@NotNull String json) {
        return VersionedComponent_1_18_R2.fromJson(json);
    }

    @Override
    public @NotNull VersionedComponent componentFromPlainText(@NotNull String text) {
        return VersionedComponent_1_18_R2.fromPlainText(text);
    }

    @Override
    public @NotNull VersionedComponent componentFromMiniMessage(@NotNull String miniMessage) {
        return VersionedComponent_1_18_R2.fromMiniMessage(miniMessage);
    }

    @Override
    public @NotNull VersionedComponent componentFromMiniMessage(@NotNull String miniMessage, @NotNull TextPlaceholder... placeholders) {
        return VersionedComponent_1_18_R2.fromMiniMessage(miniMessage, placeholders);
    }

    @Override
    public @NotNull VersionedComponent componentFromLegacyAmpersand(@NotNull String legacy) {
        return VersionedComponent_1_18_R2.fromLegacyAmpersand(legacy);
    }

    @Override
    public @NotNull VersionedComponent componentFromLegacySection(@NotNull String legacy) {
        return VersionedComponent_1_18_R2.fromLegacySection(legacy);
    }

    @Override
    public @NotNull ItemMeta setDisplayName(@NotNull ItemMeta meta, @Nullable VersionedComponent component) {
        return VersionedComponent_1_18_R2.setDisplayName(meta, component);
    }

    @Override
    public @Nullable VersionedComponent getDisplayName(@NotNull ItemMeta meta) {
        return VersionedComponent_1_18_R2.getDisplayName(meta);
    }

    @Override
    public @NotNull ItemMeta setLore(@NotNull ItemMeta meta, @Nullable List<VersionedComponent> lore) {
        return VersionedComponent_1_18_R2.setLore(meta, lore);
    }

    @Override
    public @Nullable List<VersionedComponent> getLore(@NotNull ItemMeta meta) {
        return VersionedComponent_1_18_R2.getLore(meta);
    }

    @Override
    public @NotNull ItemMeta addLoreLine(@NotNull ItemMeta meta, @NotNull VersionedComponent component) {
        return VersionedComponent_1_18_R2.addLoreLine(meta, component);
    }

}
