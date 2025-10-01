package com.kamikazejam.kamicommon.nms.wrapper;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.wrappers.NMSWrapper;
import com.kamikazejam.kamicommon.nms.wrappers.world.*;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Version-specific wrapper factory for creating NMS world instances.
 * <p>
 * This wrapper extends the {@link NMSWrapper} pattern to provide context-dependent
 * NMS world creation based on both the current Minecraft version and a specific
 * Bukkit world instance. It integrates with the block utility provider system
 * to ensure that each world wrapper has access to appropriate high-performance
 * block manipulation capabilities.
 * </p>
 * <p>
 * The wrapper handles the evolution of world management across Minecraft versions:
 * <ul>
 * <li><strong>1.8-1.12:</strong> Legacy world systems with stable NMS structure</li>
 * <li><strong>1.13+:</strong> Updated world handling with flattened block systems</li>
 * <li><strong>1.17+:</strong> Significant NMS restructuring affecting world access</li>
 * <li><strong>1.20.5+:</strong> Mojang-mapped implementations for enhanced stability</li>
 * </ul>
 * </p>
 * <p>
 * Each world wrapper provides access to:
 * <ul>
 * <li>Chunk management and manipulation systems</li>
 * <li>High-performance block operations</li>
 * <li>Entity spawning and world queries</li>
 * <li>World boundary information (min/max height)</li>
 * <li>Direct client update mechanisms</li>
 * </ul>
 * </p>
 * <p>
 * The wrapper ensures that each world instance is paired with the appropriate
 * block utility implementation, enabling consistent performance characteristics
 * and API compatibility across all supported Minecraft versions.
 * </p>
 *
 * @see NMSWorld
 * @see com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil
 */
public class NMSWorldWrapper extends NMSWrapper<NMSWorld, World> {

    /**
     * {@inheritDoc}
     * <p>
     * Creates a version-appropriate NMS world wrapper for the specified Bukkit world.
     * Each wrapper is integrated with the corresponding block utility provider to
     * ensure consistent performance and API compatibility. The selection covers
     * major world system changes including chunk handling, entity management,
     * and block manipulation optimizations.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @param world the Bukkit {@link World} to wrap with NMS functionality
     * @return the version-appropriate {@link NMSWorld} wrapper
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
    @Override
    protected @NotNull NMSWorld provide(int ver, @NotNull World world) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return new NMSWorld_1_8_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.8.3")) {
            return new NMSWorld_1_8_R2(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.8.8")) {
            return new NMSWorld_1_8_R3(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.9.2")) {
            return new NMSWorld_1_9_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.9.4")) {
            return new NMSWorld_1_9_R2(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.10.2")) {
            return new NMSWorld_1_10_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.11.2")) {
            return new NMSWorld_1_11_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.12.2")) {
            return new NMSWorld_1_12_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.13")) {
            return new NMSWorld_1_13_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.13.2")) {
            return new NMSWorld_1_13_R2(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.14.4")) {
            return new NMSWorld_1_14_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.15.2")) {
            return new NMSWorld_1_15_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.16.1")) {
            return new NMSWorld_1_16_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.16.3")) {
            return new NMSWorld_1_16_R2(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.16.5")) {
            return new NMSWorld_1_16_R3(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.17.1")) {
            return new NMSWorld_1_17_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.18.1")) {
            return new NMSWorld_1_18_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.18.2")) {
            return new NMSWorld_1_18_R2(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.19.2")) {
            return new NMSWorld_1_19_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.19.3")) {
            return new NMSWorld_1_19_R2(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.19.4")) {
            return new NMSWorld_1_19_R3(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.20.1")) {
            return new NMSWorld_1_20_R1(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.20.2")) {
            return new NMSWorld_1_20_R2(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.20.4")) {
            return new NMSWorld_1_20_R3(world, NmsAPI.getBlockUtilProvider());
        }else if (ver <= f("1.21.4")) {
            // For up to 1.21.4 (breaks in 1.21.5)
            return new NMSWorld_1_21_4(world, NmsAPI.getBlockUtilProvider());
        }
        // Includes chunk code for 1.21.5+
        return new NMSWorld_LATEST(world, NmsAPI.getBlockUtilProvider());
    }
}
