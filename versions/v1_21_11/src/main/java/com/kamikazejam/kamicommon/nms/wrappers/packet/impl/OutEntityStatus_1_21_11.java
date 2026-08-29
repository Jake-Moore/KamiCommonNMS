package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import com.kamikazejam.kamicommon.nms.reflection.FieldHandle;
import com.kamikazejam.kamicommon.nms.reflection.FieldHandles;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
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

@SuppressWarnings("unchecked")
public class OutEntityStatus_1_21_11 implements NMSOutEntityStatus {
    private static final FieldHandle<Integer> entityHandle = (FieldHandle<Integer>) FieldHandles.getHandle("entityId", ClientboundEntityEventPacket.class);

    private final @NotNull ClientboundEntityEventPacket packet;

    public OutEntityStatus_1_21_11(@NotNull ClientboundEntityEventPacket packet) {
        this.packet = packet;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.packet;
    }

    @Override
    public int getEntityID() {
        return OutEntityStatus_1_21_11.entityHandle.get(this.packet);
    }

    @Override
    public byte getStatus() {
        return this.packet.getEventId();
    }
}
