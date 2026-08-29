package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// Verified on 1.20.CB and 1.21 and 1.21.3
/**
 * <p><b>Its twin is {@code NmsItemMethods_1_21_11}, and the source is deliberately the same.</b>
 * That one is compiled against 1.21.11 and targets Java 21, because servers from 1.20.6 to 1.21.11
 * run on Java 21 and could not load a Java 25 class. This one targets Java 25, because 26.x
 * requires it. The ladder in {@code :core} sends everything up to 1.21.11 to the twin, and
 * everything above it here.</p>
 *
 * <p><b>Do not delete this copy in favour of the twin.</b> {@code v_latest} holds one of every
 * provider on purpose. It is the only module compiled against the newest Paper, so bumping {@code
 * highestPaperDep} compile-checks every provider against it. Dropping one from here gives up the
 * warning that a 26.x API change has broken it.</p>
 *
 * <p>Keep the two in step. If you change one, change the other.</p>
 */
@SuppressWarnings("unused")
public class NmsItemMethods_LATEST implements NmsItemMethods {
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull String getI18NItemName(@NotNull ItemStack itemStack) {
        return Objects.requireNonNull(itemStack.getI18NDisplayName());
    }
}
