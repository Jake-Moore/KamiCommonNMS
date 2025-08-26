package com.kamikazejam.kamicommon.nms.wrappers.packet;

import com.kamikazejam.kamicommon.nms.wrappers.packet.impl.NMSOutEntityDestroy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handler interface for NMS packet operations and management.
 * <p>
 * This interface provides the primary API for working with Minecraft packets,
 * including wrapping raw NMS packet objects, creating new packets, and sending
 * packets to players. It abstracts away the complexity of version-specific
 * packet handling while providing a unified interface for packet operations.
 * </p>
 * <p>
 * The packet handler serves as both a factory for creating packet wrappers
 * and as a communication bridge for sending packets to clients. It handles
 * the intricate details of packet serialization, version compatibility,
 * and network transmission.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Wrap an existing NMS packet
 * NMSPacket wrappedPacket = handler.wrapPacket(rawNMSPacket);
 * 
 * // Create a new packet
 * NMSOutEntityDestroy destroyPacket = handler.createDestroyPacket(1, 2, 3);
 * 
 * // Send packet to player
 * handler.sendPacket(player, destroyPacket);
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public interface NMSPacketHandler {
    /**
     * Wraps a raw NMS packet object in a type-safe wrapper.
     * <p>
     * This method takes a raw NMS packet object and creates an appropriate
     * wrapper that implements the {@link NMSPacket} interface. The wrapper
     * provides type-safe access to packet data and functionality while
     * hiding version-specific implementation details.
     * </p>
     * <p>
     * The method automatically detects the packet type and creates the
     * most appropriate wrapper implementation. If the packet type is not
     * supported or recognized, an {@link IllegalStateException} is thrown.
     * </p>
     *
     * @param packet the raw NMS packet object to wrap
     * @return a {@link NMSPacket} wrapper for the specified packet
     * @throws IllegalStateException if the packet type is unsupported or invalid
     */
    @NotNull
    NMSPacket wrapPacket(@NotNull Object packet) throws IllegalStateException;

    /**
     * Creates a new entity destroy packet for the specified entity IDs.
     * <p>
     * This method creates a packet that, when sent to a client, will cause
     * the specified entities to be removed from the client's view. This is
     * commonly used for despawning entities, hiding entities from specific
     * players, or cleaning up entity displays.
     * </p>
     * <p>
     * The created packet can be sent to one or more players using the
     * {@link #sendPacket(Player, NMSPacket)} method.
     * </p>
     *
     * @param ids the entity IDs to include in the destroy packet
     * @return a new {@link NMSOutEntityDestroy} packet containing the specified entity IDs
     */
    @NotNull
    NMSOutEntityDestroy createDestroyPacket(int... ids);

    /**
     * Sends a packet to the specified player.
     * <p>
     * This method transmits the specified packet to the given player's client,
     * handling all the necessary serialization and network communication details.
     * The packet is sent asynchronously and does not block the calling thread.
     * </p>
     * <p>
     * The method works with any {@link NMSPacket} wrapper, automatically
     * extracting the underlying NMS packet and handling version-specific
     * transmission requirements.
     * </p>
     *
     * @param player the {@link Player} to send the packet to
     * @param nmsPacket the {@link NMSPacket} to send
     */
    void sendPacket(@NotNull Player player, @NotNull NMSPacket nmsPacket);
}
