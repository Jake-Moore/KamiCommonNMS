package com.kamikazejam.kamicommon.nms.bundle.v1_21_9;

import org.jetbrains.annotations.ApiStatus;
import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundle;
import com.kamikazejam.kamicommon.nms.teleport.Teleporter1_21_9;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code v1_21_9} module's implementation of {@link NmsBundle}.
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
    public @NotNull AbstractTeleporter teleporter() {
        return new Teleporter1_21_9();
    }
}
