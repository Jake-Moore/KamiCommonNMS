package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.teleport.*;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific teleportation implementations.
 * <p>
 * This provider selects the appropriate {@link AbstractTeleporter} implementation
 * based on the current Minecraft version, handling the evolution of teleportation
 * systems and NMS player manipulation APIs. Teleporters provide direct NMS-based
 * teleportation that bypasses Bukkit events for performance-critical operations.
 * </p>
 * <p>
 * The provider covers major changes in teleportation handling:
 * <ul>
 * <li><strong>1.8-1.12:</strong> Legacy teleportation with stable NMS player access</li>
 * <li><strong>1.13+:</strong> Updated player handling with protocol improvements</li>
 * <li><strong>1.17+:</strong> Significant NMS restructuring affecting player manipulation</li>
 * <li><strong>1.20.5+:</strong> Mojang-mapped Paper NMS and CraftBukkit implementations</li>
 * </ul>
 * </p>
 * <p>
 * These implementations provide high-performance teleportation that bypasses normal
 * Bukkit event processing, making them suitable for scenarios requiring rapid
 * player movement without triggering standard teleportation events or validation.
 * </p>
 *
 * @see AbstractTeleporter
 */
public class TeleportProvider extends Provider<AbstractTeleporter> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the appropriate teleporter implementation based on Minecraft version,
     * accounting for changes in player handling and NMS structure. The selection
     * includes specific handling for the transition to Mojang-mapped implementations.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractTeleporter} implementation
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
    @Override
    protected @NotNull AbstractTeleporter provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return new Teleporter1_8_R1();
        }else if (ver <= f("1.8.3")) {
            return new Teleporter1_8_R2();
        }else if (ver <= f("1.8.8")) {
            return new Teleporter1_8_R3();
        }else if (ver <= f("1.9.2")) {
            return new Teleporter1_9_R1();
        }else if (ver <= f("1.9.4")) {
            return new Teleporter1_9_R2();
        }else if (ver <= f("1.10.2")) {
            return new Teleporter1_10_R1();
        }else if (ver <= f("1.11.2")) {
            return new Teleporter1_11_R1();
        }else if (ver <= f("1.12.2")) {
            return new Teleporter1_12_R1();
        }else if (ver <= f("1.13")) {
            return new Teleporter1_13_R1();
        }else if (ver <= f("1.13.2")) {
            return new Teleporter1_13_R2();
        }else if (ver <= f("1.14.4")) {
            return new Teleporter1_14_R1();
        }else if (ver <= f("1.15.2")) {
            return new Teleporter1_15_R1();
        }else if (ver <= f("1.16.1")) {
            return new Teleporter1_16_R1();
        }else if (ver <= f("1.16.3")) {
            return new Teleporter1_16_R2();
        }else if (ver <= f("1.16.5")) {
            return new Teleporter1_16_R3();
        }else if (ver <= f("1.17.1")) {
            return new Teleporter1_17_R1();
        }else if (ver <= f("1.18.1")) {
            return new Teleporter1_18_R1();
        }else if (ver <= f("1.18.2")) {
            return new Teleporter1_18_R2();
        }else if (ver <= f("1.19.2")) {
            return new Teleporter1_19_R1();
        }else if (ver <= f("1.19.3")) {
            return new Teleporter1_19_R2();
        }else if (ver <= f("1.19.4")) {
            return new Teleporter1_19_R3();
        }else if (ver <= f("1.20.1")) {
            return new Teleporter1_20_R1();
        }else if (ver <= f("1.20.2")) {
            return new Teleporter1_20_R2();
        }else if (ver <= f("1.20.4")) {
            return new Teleporter1_20_R3();
        }else if (ver <= f("1.20.6")) {
            // Confirmed for 1.20.5 and 1.20.6
            return new Teleporter1_20_CB();
        }
        // Confirmed for 1.21
        return new Teleporter1_21_CB();
    }
}
