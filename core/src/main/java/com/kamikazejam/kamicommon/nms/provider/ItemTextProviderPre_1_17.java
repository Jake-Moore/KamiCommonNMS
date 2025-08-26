package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import com.kamikazejam.kamicommon.nms.itemtext.*;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific item text implementations (Pre-1.17 only).
 * <p>
 * This provider selects the appropriate {@link AbstractItemTextPre_1_17} implementation
 * for Minecraft versions before 1.17, handling the evolution of item tooltip and
 * text component systems. The provider is specifically designed for pre-1.17 versions
 * that used different text component architectures.
 * </p>
 * <p>
 * The provider covers the pre-1.17 text component evolution:
 * <ul>
 * <li><strong>1.8-1.12:</strong> Legacy text components with basic hover support</li>
 * <li><strong>1.13-1.16:</strong> Enhanced text components with improved tooltip systems</li>
 * <li><strong>1.17+:</strong> Major text system overhaul (handled by separate providers)</li>
 * </ul>
 * </p>
 * <p>
 * This provider is used in conjunction with newer text providers to ensure
 * comprehensive coverage of item text functionality across all Minecraft versions,
 * with 1.17+ using entirely different text component systems.
 * </p>
 *
 * @see AbstractItemTextPre_1_17
 */
public class ItemTextProviderPre_1_17 extends Provider<AbstractItemTextPre_1_17> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the appropriate item text implementation for pre-1.17 versions only.
     * This provider explicitly does not support 1.17+ versions, as those use
     * fundamentally different text component systems that require separate providers.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractItemTextPre_1_17} implementation
     * @throws IllegalArgumentException if the version is below 1.8 or 1.17+ (unsupported)
     */
    @Override
    protected @NotNull AbstractItemTextPre_1_17 provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return new ItemText_1_8_R1();
        }else if (ver <= f("1.8.3")) {
            return new ItemText_1_8_R2();
        }else if (ver <= f("1.8.8")) {
            return new ItemText_1_8_R3();
        }else if (ver <= f("1.9.2")) {
            return new ItemText_1_9_R1();
        }else if (ver <= f("1.9.4")) {
            return new ItemText_1_9_R2();
        }else if (ver <= f("1.10.2")) {
            return new ItemText_1_10_R1();
        }else if (ver <= f("1.11.2")) {
            return new ItemText_1_11_R1();
        }else if (ver <= f("1.12.2")) {
            return new ItemText_1_12_R1();
        }else if (ver <= f("1.13")) {
            return new ItemText_1_13_R1();
        }else if (ver <= f("1.13.2")) {
            return new ItemText_1_13_R2();
        }else if (ver <= f("1.14.4")) {
            return new ItemText_1_14_R1();
        }else if (ver <= f("1.15.2")) {
            return new ItemText_1_15_R1();
        }else if (ver <= f("1.16.1")) {
            return new ItemText_1_16_R1();
        }else if (ver <= f("1.16.3")) {
            return new ItemText_1_16_R2();
        }else if (ver <= f("1.16.5")) {
            return new ItemText_1_16_R3();
        }

        throw new IllegalArgumentException("Version not supported (>=? 1.17): " + ver);
    }
}
