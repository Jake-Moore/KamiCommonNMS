package com.kamikazejam.kamicommon.nms.provider;

import org.jetbrains.annotations.ApiStatus;
import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundles;
import com.kamikazejam.kamicommon.nms.text.ComponentLoggerAdapter;
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
@ApiStatus.Internal
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
        if (ver < f("1.18.2")) {
            // Before Paper bundled Adventure there is one legacy implementation for every
            // version, so it lives once in v1_16_R3 at Java 8, the lowest floor any server
            // in this range needs. Splitting it per NMS revision would only duplicate it.
            return NmsBundles.forModule("v1_16_R3").componentLoggerAdapter();
        }
        // 1.18.2 is where Paper exposes the native ComponentLogger; those servers run Java 17.
        return NmsBundles.forModule("v1_18_R2").componentLoggerAdapter();
    }
}
