package com.kamikazejam.kamicommon.nms.bundle.v1_13_R1;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.block.BlockUtil1_13_R1;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.entity.EntityMethods_1_13_R1;
import com.kamikazejam.kamicommon.nms.item.NmsItemMethods_1_13_R1;
import com.kamikazejam.kamicommon.nms.itemtext.ItemText_1_13_R1;
import com.kamikazejam.kamicommon.nms.provider.Provider;
import com.kamikazejam.kamicommon.nms.teleport.Teleporter1_13_R1;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler;
import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacketHandler_1_13_R1;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld_1_13_R1;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code v1_13_R1} module's implementation of {@link NmsBundle}.
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
public class NmsBundleImpl implements NmsBundle {

    @Override
    public @NotNull AbstractBlockUtil blockUtil() {
        return new BlockUtil1_13_R1();
    }

    @Override
    public @NotNull AbstractEntityMethods entityMethods() {
        return new EntityMethods_1_13_R1();
    }

    @Override
    public @NotNull AbstractItemTextPre_1_17 itemText() {
        return new ItemText_1_13_R1();
    }

    @Override
    public @NotNull NmsItemMethods nmsItemMethods() {
        return new NmsItemMethods_1_13_R1();
    }

    @Override
    public @NotNull NMSWorld nmsWorld(@NotNull World world, @NotNull Provider<AbstractBlockUtil> blockUtil) {
        return new NMSWorld_1_13_R1(world, blockUtil);
    }

    @Override
    public @NotNull NMSPacketHandler packetHandler() {
        return new NMSPacketHandler_1_13_R1();
    }

    @Override
    public @NotNull AbstractTeleporter teleporter() {
        return new Teleporter1_13_R1();
    }
}
