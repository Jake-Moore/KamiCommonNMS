package com.kamikazejam.kamicommon.nms.bundle.worlds6;

import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.library.worldedit.WorldEdit6;
import com.kamikazejam.kamicommon.nms.library.worldedit.WorldEditApi;
import com.kamikazejam.kamicommon.nms.library.worldguard.WorldGuard6;
import com.kamikazejam.kamicommon.nms.library.worldguard.WorldGuardApi;
import com.kamikazejam.kamicommon.nms.wrappers.NMSWrapper;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code worlds6} module's implementation of {@link NmsBundle}.
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
    public @NotNull WorldEditApi<?> worldEdit() {
        return new WorldEdit6();
    }

    @Override
    public @NotNull WorldGuardApi worldGuard(@NotNull Plugin worldGuardPlugin, @NotNull NMSWrapper<NMSWorld, World> worldWrapper) {
        return new WorldGuard6(worldGuardPlugin, worldWrapper);
    }
}
