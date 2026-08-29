package com.kamikazejam.kamicommon.nms.wrappers.packet;

import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.NMSOutEntityDestroy;
import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.OutEntityDestroy_1_21_11;
import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.OutEntityStatus_1_21_11;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
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
public class NMSPacketHandler_1_21_11 implements NMSPacketHandler {

    @Override
    public @NotNull NMSPacket wrapPacket(@NotNull Object packet) {
        if (packet instanceof ClientboundEntityEventPacket) {
            return new OutEntityStatus_1_21_11((ClientboundEntityEventPacket) packet);
        }
        throw new IllegalArgumentException("Unknown packet type: " + packet.getClass().getName());
    }

    @Override
    public @NotNull NMSOutEntityDestroy createDestroyPacket(final int... ids) {
        return new OutEntityDestroy_1_21_11(new ClientboundRemoveEntitiesPacket(ids));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void sendPacket(@NotNull Player player, @NotNull NMSPacket packet) {
        ((CraftPlayer) player).getHandle().connection.send((Packet) packet.getHandle());
    }
}
