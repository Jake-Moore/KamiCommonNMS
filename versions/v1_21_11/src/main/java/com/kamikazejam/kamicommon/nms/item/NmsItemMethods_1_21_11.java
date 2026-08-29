package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// Verified on 1.20.CB and 1.21 and 1.21.3
/**
 * The pre-26 copy of this implementation, compiled against the 1.21.11 dev bundle.
 *
 * <p>Its twin in {@code v_latest} is compiled against Paper 26.x and targets Java 25, because
 * 26.x requires Java 25. This one exists so that servers from 1.20.6 through 1.21.11, which run
 * on Java 21, still have an implementation they can load. The source is the same; only the dev
 * bundle and the JVM target differ, which is the same fork this library makes at every NMS
 * boundary, applied to a JVM boundary.</p>
 *
 * <p>Do not delete the {@code v_latest} copy in favour of this one. That module holds one of
 * every provider so that bumping the newest Paper dev bundle compile-checks them all, which is
 * how a 26.x API change gets noticed before it reaches a server.</p>
 *
 * <p>Keep the two in step. If you change one, change the other.</p>
 */
@SuppressWarnings("unused")
public class NmsItemMethods_1_21_11 implements NmsItemMethods {
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull String getI18NItemName(@NotNull ItemStack itemStack) {
        return Objects.requireNonNull(itemStack.getI18NDisplayName());
    }
}
