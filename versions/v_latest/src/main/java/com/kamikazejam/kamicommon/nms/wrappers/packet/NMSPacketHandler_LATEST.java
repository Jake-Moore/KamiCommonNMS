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
