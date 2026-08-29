package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import com.kamikazejam.kamicommon.nms.reflection.FieldHandle;
import com.kamikazejam.kamicommon.nms.reflection.FieldHandles;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import org.jetbrains.annotations.NotNull;
/**
 * <p><b>Its twin is {@code OutEntityStatus_1_21_11}, and the source is deliberately the same.</b>
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

@SuppressWarnings("unchecked")
public class OutEntityStatus_LATEST implements NMSOutEntityStatus {
    private static final FieldHandle<Integer> entityHandle = (FieldHandle<Integer>) FieldHandles.getHandle("entityId", ClientboundEntityEventPacket.class);

    private final @NotNull ClientboundEntityEventPacket packet;

    public OutEntityStatus_LATEST(@NotNull ClientboundEntityEventPacket packet) {
        this.packet = packet;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.packet;
    }

    @Override
    public int getEntityID() {
        return OutEntityStatus_LATEST.entityHandle.get(this.packet);
    }

    @Override
    public byte getStatus() {
        return this.packet.getEventId();
    }
}
