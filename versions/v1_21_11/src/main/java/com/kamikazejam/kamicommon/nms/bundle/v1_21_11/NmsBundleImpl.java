package com.kamikazejam.kamicommon.nms.bundle.v1_21_11;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.provider.Provider;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld_1_21_11;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code v1_21_11} module's implementation of {@link NmsBundle}.
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
    public @NotNull NMSWorld nmsWorld(@NotNull World world, @NotNull Provider<AbstractBlockUtil> blockUtil) {
        return new NMSWorld_1_21_11(world, blockUtil);
    }
}
