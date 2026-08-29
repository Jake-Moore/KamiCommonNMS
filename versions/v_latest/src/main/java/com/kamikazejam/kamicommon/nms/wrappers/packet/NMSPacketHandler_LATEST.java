package com.kamikazejam.kamicommon.nms.wrappers.packet;

import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.NMSOutEntityDestroy;
import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.OutEntityDestroy_LATEST;
import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.OutEntityStatus_LATEST;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
/**
 * <p><b>Its twin is {@code NMSPacketHandler_1_21_11}, and the source is deliberately the same.</b>
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
public class NMSPacketHandler_LATEST implements NMSPacketHandler {

    @Override
    public @NotNull NMSPacket wrapPacket(@NotNull Object packet) {
        if (packet instanceof ClientboundEntityEventPacket) {
            return new OutEntityStatus_LATEST((ClientboundEntityEventPacket) packet);
        }
        throw new IllegalArgumentException("Unknown packet type: " + packet.getClass().getName());
    }

    @Override
    public @NotNull NMSOutEntityDestroy createDestroyPacket(final int... ids) {
        return new OutEntityDestroy_LATEST(new ClientboundRemoveEntitiesPacket(ids));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void sendPacket(@NotNull Player player, @NotNull NMSPacket packet) {
        ((CraftPlayer) player).getHandle().connection.send((Packet) packet.getHandle());
    }
}
