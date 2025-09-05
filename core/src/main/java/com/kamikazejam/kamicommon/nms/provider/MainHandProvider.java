package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.mainhand.AbstractMainHand;
import com.kamikazejam.kamicommon.nms.mainhand.MainHand_1_8_R1;
import com.kamikazejam.kamicommon.nms.mainhand.MainHand_LATEST;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific main hand implementations.
 * <p>
 * This provider selects the appropriate {@link AbstractMainHand} implementation
 * based on the current Minecraft version, handling the introduction of dual-wielding
 * mechanics in Minecraft 1.9. Main hand providers manage the distinction between
 * main hand and off-hand items across different Minecraft versions.
 * </p>
 * <p>
 * The provider addresses the major gameplay change in Minecraft 1.9:
 * <ul>
 * <li><strong>1.8 and earlier:</strong> Single-hand system with no off-hand support</li>
 * <li><strong>1.9+:</strong> Dual-wielding system with main hand and off-hand slots</li>
 * </ul>
 * </p>
 * <p>
 * This enables consistent item handling across versions while providing access
 * to dual-wielding features on supported versions and graceful fallback behavior
 * on older versions that only support single-hand gameplay.
 * </p>
 *
 * @see AbstractMainHand
 */
public class MainHandProvider extends Provider<AbstractMainHand> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the main hand implementation based on dual-wielding support availability.
     * Versions 1.9+ support dual-wielding mechanics, while earlier versions use
     * single-hand systems with appropriate fallback behavior.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractMainHand} implementation
     */
    @Override
    protected @NotNull AbstractMainHand provide(int ver) {
        if (ver <= 1090) {
            return new MainHand_1_8_R1();
        }
        return new MainHand_LATEST();
    }
}
