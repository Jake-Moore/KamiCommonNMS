package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemNbt;
import com.kamikazejam.kamicommon.nms.bundle.NmsBundles;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for the item NBT an Adventure {@code show_item} hover is built from.
 * <p>
 * Covers 1.8 through 1.18.1 and throws above it. From 1.18.2 a server has its own Adventure and
 * hands the hover over through {@code ItemStack.asHoverEvent()}, so nothing on those versions asks
 * for this.
 * </p>
 * <p>
 * Below 1.17 the implementation is the one
 * {@link com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17} already
 * carries, so those branches resolve the same adapter the item text provider does. 1.17 and 1.18.1
 * are separate modules because their mappings differ.
 * </p>
 * <p>
 * This lives in {@code :api} rather than {@code :core} for the same reason
 * {@link ItemTextProviderPre_1_17} does: it is resolved from inside the nested Adventure jar, whose
 * classloader can see {@code :api} by parent-first delegation and cannot see {@code :core}.
 * </p>
 *
 * @see AbstractItemNbt
 */
public class ItemNbtProvider extends Provider<AbstractItemNbt> {
    /**
     * {@inheritDoc}
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractItemNbt} implementation
     * @throws IllegalArgumentException if the version is below 1.8 or 1.18.2 and above
     */
    @Override
    protected @NotNull AbstractItemNbt provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return NmsBundles.forModule("v1_8_R1").itemText();
        }else if (ver <= f("1.8.3")) {
            return NmsBundles.forModule("v1_8_R2").itemText();
        }else if (ver <= f("1.8.8")) {
            return NmsBundles.forModule("v1_8_R3").itemText();
        }else if (ver <= f("1.9.2")) {
            return NmsBundles.forModule("v1_9_R1").itemText();
        }else if (ver <= f("1.9.4")) {
            return NmsBundles.forModule("v1_9_R2").itemText();
        }else if (ver <= f("1.10.2")) {
            return NmsBundles.forModule("v1_10_R1").itemText();
        }else if (ver <= f("1.11.2")) {
            return NmsBundles.forModule("v1_11_R1").itemText();
        }else if (ver <= f("1.12.2")) {
            return NmsBundles.forModule("v1_12_R1").itemText();
        }else if (ver <= f("1.13")) {
            return NmsBundles.forModule("v1_13_R1").itemText();
        }else if (ver <= f("1.13.2")) {
            return NmsBundles.forModule("v1_13_R2").itemText();
        }else if (ver <= f("1.14.4")) {
            return NmsBundles.forModule("v1_14_R1").itemText();
        }else if (ver <= f("1.15.2")) {
            return NmsBundles.forModule("v1_15_R1").itemText();
        }else if (ver <= f("1.16.1")) {
            return NmsBundles.forModule("v1_16_R1").itemText();
        }else if (ver <= f("1.16.3")) {
            return NmsBundles.forModule("v1_16_R2").itemText();
        }else if (ver <= f("1.16.5")) {
            return NmsBundles.forModule("v1_16_R3").itemText();
        }else if (ver <= f("1.17.1")) {
            // 1.17 runs Java 16, so this cannot be v1_18_R1 (floor 17).
            return NmsBundles.forModule("v1_17_R1").itemNbt();
        }else if (ver <= f("1.18.1")) {
            return NmsBundles.forModule("v1_18_R1").itemNbt();
        }

        throw new IllegalArgumentException("Version not supported (>= 1.18.2): " + ver);
    }
}
