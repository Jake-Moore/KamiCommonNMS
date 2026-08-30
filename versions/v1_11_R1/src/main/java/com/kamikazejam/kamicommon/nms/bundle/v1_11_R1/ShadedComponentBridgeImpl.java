package com.kamikazejam.kamicommon.nms.bundle.v1_11_R1;

import com.kamikazejam.kamicommon.nms.bundle.ShadedComponentBridge;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent_1_11_R1;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Kept out of {@code NmsBundleImpl} on purpose. Loading a module's adapter must not resolve the
 * shaded Adventure copy, because a 26.x server loads adapters for capabilities that have nothing to
 * do with text. See {@link ShadedComponentBridge}.
 */
public class ShadedComponentBridgeImpl implements ShadedComponentBridge {

    @Override
    public @NotNull VersionedComponent componentFrom(@NotNull Component component) {
        return new VersionedComponent_1_11_R1(component);
    }
}
