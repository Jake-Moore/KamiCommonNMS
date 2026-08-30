package com.kamikazejam.kamicommon.nms.provider;

import org.jetbrains.annotations.ApiStatus;
import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundles;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific NMS item method implementations.
 * <p>
 * This provider selects the appropriate {@link NmsItemMethods} implementation
 * based on the current Minecraft version, handling the evolution of item systems
 * and internationalization (i18n) naming conventions. NMS item methods provide
 * access to internal item names and properties not available through Bukkit APIs.
 * </p>
 * <p>
 * The provider covers major changes in item handling systems:
 * <ul>
 * <li><strong>1.8-1.12:</strong> Legacy item systems with basic i18n support</li>
 * <li><strong>1.13+:</strong> Flattened item system with updated naming conventions</li>
 * <li><strong>1.17+:</strong> Significant NMS restructuring affecting item access</li>
 * <li><strong>1.20.5+:</strong> Mojang-mapped Paper NMS for improved stability</li>
 * </ul>
 * </p>
 * <p>
 * These implementations provide access to internal item names, translation keys,
 * and other properties that are essential for plugins requiring deep item inspection
 * or compatibility with external systems expecting specific item identifiers.
 * </p>
 *
 * @see NmsItemMethods
 */
@ApiStatus.Internal
public class NmsItemProvider extends Provider<NmsItemMethods> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the appropriate NMS item methods implementation based on Minecraft version,
     * accounting for the evolution of item systems and naming conventions. The selection
     * ensures access to the most appropriate item inspection methods for each version.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link NmsItemMethods} implementation
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
    @Override
    protected @NotNull NmsItemMethods provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return NmsBundles.forModule("v1_8_R1").nmsItemMethods();
        }else if (ver <= f("1.8.3")) {
            return NmsBundles.forModule("v1_8_R2").nmsItemMethods();
        }else if (ver <= f("1.8.8")) {
            return NmsBundles.forModule("v1_8_R3").nmsItemMethods();
        }else if (ver <= f("1.9.2")) {
            return NmsBundles.forModule("v1_9_R1").nmsItemMethods();
        }else if (ver <= f("1.9.4")) {
            return NmsBundles.forModule("v1_9_R2").nmsItemMethods();
        }else if (ver <= f("1.10.2")) {
            return NmsBundles.forModule("v1_10_R1").nmsItemMethods();
        }else if (ver <= f("1.11.2")) {
            return NmsBundles.forModule("v1_11_R1").nmsItemMethods();
        }else if (ver <= f("1.12.2")) {
            return NmsBundles.forModule("v1_12_R1").nmsItemMethods();
        }else if (ver <= f("1.13")) {
            return NmsBundles.forModule("v1_13_R1").nmsItemMethods();
        }else if (ver <= f("1.13.2")) {
            return NmsBundles.forModule("v1_13_R2").nmsItemMethods();
        }else if (ver <= f("1.14.4")) {
            return NmsBundles.forModule("v1_14_R1").nmsItemMethods();
        }else if (ver <= f("1.15.2")) {
            return NmsBundles.forModule("v1_15_R1").nmsItemMethods();
        }else if (ver <= f("1.16.1")) {
            return NmsBundles.forModule("v1_16_R1").nmsItemMethods();
        }else if (ver <= f("1.16.3")) {
            return NmsBundles.forModule("v1_16_R2").nmsItemMethods();
        }else if (ver <= f("1.16.5")) {
            return NmsBundles.forModule("v1_16_R3").nmsItemMethods();
        }else if (ver <= f("1.17.1")) {
            return NmsBundles.forModule("v1_17_R1").nmsItemMethods();
        }else if (ver <= f("1.18.1")) {
            return NmsBundles.forModule("v1_18_R1").nmsItemMethods();
        }else if (ver <= f("1.18.2")) {
            return NmsBundles.forModule("v1_18_R2").nmsItemMethods();
        }else if (ver <= f("1.19.2")) {
            return NmsBundles.forModule("v1_19_R1").nmsItemMethods();
        }else if (ver <= f("1.19.3")) {
            return NmsBundles.forModule("v1_19_R2").nmsItemMethods();
        }else if (ver <= f("1.19.4")) {
            return NmsBundles.forModule("v1_19_R3").nmsItemMethods();
        }else if (ver <= f("1.20.1")) {
            return NmsBundles.forModule("v1_20_R1").nmsItemMethods();
        }else if (ver <= f("1.20.2")) {
            return NmsBundles.forModule("v1_20_R2").nmsItemMethods();
        }else if (ver <= f("1.20.4")) {
            return NmsBundles.forModule("v1_20_R3").nmsItemMethods();
        }

        // Everything below 26.x runs on Java 21 or lower, so it comes from v1_21_11. The
        // v_latest twin below is the same source compiled against Paper 26.x, which targets
        // Java 25 because 26.x requires it. Routing a 1.21 server there would fail to load.
        if (ver < f("26")) {
            return NmsBundles.forModule("v1_21_11").nmsItemMethods();
        }
        // 26.x only, and compiled against it.
        return NmsBundles.forModule("v_latest").nmsItemMethods();
    }
}

