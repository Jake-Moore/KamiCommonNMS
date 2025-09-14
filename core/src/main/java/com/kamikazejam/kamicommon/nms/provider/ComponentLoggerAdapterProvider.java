package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.text.ComponentLoggerAdapter;
import com.kamikazejam.kamicommon.nms.text.ComponentLoggerAdapter_1_18_R1;
import com.kamikazejam.kamicommon.nms.text.ComponentLoggerAdapter_LATEST;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific server component logging adapter.
 * <p>
 * This provider selects the appropriate {@link ComponentLoggerAdapter} implementation
 * based on the current Minecraft version
 * </p>
 *
 * @see ComponentLoggerAdapter
 */
public class ComponentLoggerAdapterProvider extends Provider<ComponentLoggerAdapter> {
    /**
     * {@inheritDoc}
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractBlockUtil} implementation
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
    @Override
    protected @NotNull ComponentLoggerAdapter provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Select the correct wrapper which knows how to send this kind of component
        if (ver <= f("1.18.1")) {
            // uses legacy logging method, since server does not have kyori ComponentLogger yet
            return new ComponentLoggerAdapter_1_18_R1();
        }

        // 1.18.2+ has adventure and MiniMessage bundled, so we can use the native apis
        return new ComponentLoggerAdapter_LATEST();
    }
}
