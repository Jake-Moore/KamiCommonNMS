package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.jetbrains.annotations.NotNull;
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
public class OutEntityDestroy_1_21_11 implements NMSOutEntityDestroy {
    private final @NotNull ClientboundRemoveEntitiesPacket packet;

    public OutEntityDestroy_1_21_11(@NotNull ClientboundRemoveEntitiesPacket packet) {
        this.packet = packet;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.packet;
    }

    @Override
    public int[] getToDestroy() {
        return this.packet.getEntityIds().toIntArray();
    }
}
