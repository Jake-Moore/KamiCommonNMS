package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.block.*;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific block utility implementations.
 * <p>
 * This provider selects the appropriate {@link AbstractBlockUtil} implementation
 * based on the current Minecraft version, handling the evolution of NMS block
 * manipulation APIs across different Minecraft releases. Block utilities provide
 * high-performance block placement operations that bypass normal Bukkit overhead.
 * </p>
 * <p>
 * The provider covers major API changes in Minecraft's block handling system:
 * <ul>
 * <li><strong>1.8-1.12:</strong> Legacy block manipulation with data values</li>
 * <li><strong>1.13+:</strong> Flattened block system with block states</li>
 * <li><strong>1.17+:</strong> Significant NMS restructuring and mapping changes</li>
 * <li><strong>1.20.5+:</strong> Mojang-mapped Paper NMS for improved stability</li>
 * </ul>
 * </p>
 * <p>
 * Each implementation provides optimized block placement methods that can significantly
 * outperform standard Bukkit block operations, particularly for bulk operations
 * like world generation, schematic pasting, or large-scale modifications.
 * </p>
 *
 * @see AbstractBlockUtil
 */
public class BlockUtilProvider extends Provider<AbstractBlockUtil> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the appropriate block utility implementation based on Minecraft version,
     * accounting for major API changes in block handling systems. The selection
     * covers the evolution from legacy data values to modern block states.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractBlockUtil} implementation
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
    @Override
    protected @NotNull AbstractBlockUtil provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return new BlockUtil1_8_R1();
        }else if (ver <= f("1.8.3")) {
            return new BlockUtil1_8_R2();
        }else if (ver <= f("1.8.8")) {
            return new BlockUtil1_8_R3();
        }else if (ver <= f("1.9.2")) {
            return new BlockUtil1_9_R1();
        }else if (ver <= f("1.9.4")) {
            return new BlockUtil1_9_R2();
        }else if (ver <= f("1.10.2")) {
            return new BlockUtil1_10_R1();
        }else if (ver <= f("1.11.2")) {
            return new BlockUtil1_11_R1();
        }else if (ver <= f("1.12.2")) {
            return new BlockUtil1_12_R1();
        }else if (ver <= f("1.13")) {
            return new BlockUtil1_13_R1();
        }else if (ver <= f("1.13.2")) {
            return new BlockUtil1_13_R2();
        }else if (ver <= f("1.14.4")) {
            return new BlockUtil1_14_R1();
        }else if (ver <= f("1.15.2")) {
            return new BlockUtil1_15_R1();
        }else if (ver <= f("1.16.1")) {
            return new BlockUtil1_16_R1();
        }else if (ver <= f("1.16.3")) {
            return new BlockUtil1_16_R2();
        }else if (ver <= f("1.16.5")) {
            return new BlockUtil1_16_R3();
        }else if (ver <= f("1.17.1")) {
            return new BlockUtil1_17_R1();
        }else if (ver <= f("1.18.1")) {
            return new BlockUtil1_18_R1();
        }else if (ver <= f("1.18.2")) {
            return new BlockUtil1_18_R2();
        }else if (ver <= f("1.19.2")) {
            return new BlockUtil1_19_R1();
        }else if (ver <= f("1.19.3")) {
            return new BlockUtil1_19_R2();
        }else if (ver <= f("1.19.4")) {
            return new BlockUtil1_19_R3();
        }else if (ver <= f("1.20.1")) {
            return new BlockUtil1_20_R1();
        }else if (ver <= f("1.20.2")) {
            return new BlockUtil1_20_R2();
        }else if (ver <= f("1.20.4")) {
            return new BlockUtil1_20_R3();
        }
        // With the mojang-mapped paper nms now, we might be good to use this version indefinitely
        return new BlockUtil1_21_CB(); // Confirmed for 1.20.5, 1.20.6, 1.21
    }
}
