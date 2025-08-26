package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacket;

/**
 * NMS wrapper for outbound entity destroy packets.
 * <p>
 * This interface provides access to Minecraft's entity destroy packet,
 * which is sent from the server to clients to notify them that one or
 * more entities should be removed from their view. This packet is commonly
 * used when entities are despawned, move out of render distance, or are
 * explicitly hidden from specific players.
 * </p>
 * <p>
 * The destroy packet contains an array of entity IDs that should be removed
 * from the client's entity tracking. Once processed by the client, these
 * entities will no longer be visible or interact with the player until
 * they are respawned or come back into view.
 * </p>
 * <p>
 * This packet is particularly useful for:
 * <ul>
 * <li>Implementing custom entity visibility systems</li>
 * <li>Creating holographic displays that need to be hidden from specific players</li>
 * <li>Managing temporary entities or effects</li>
 * <li>Optimizing client performance by removing unnecessary entities</li>
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create a destroy packet for multiple entities
 * NMSOutEntityDestroy destroyPacket = packetHandler.createDestroyPacket(101, 102, 103);
 * 
 * // Get the entity IDs from an existing packet
 * int[] entityIds = destroyPacket.getToDestroy();
 * 
 * // Send to player
 * packetHandler.sendPacket(player, destroyPacket);
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public interface NMSOutEntityDestroy extends NMSPacket {
    /**
     * Retrieves the array of entity IDs to be destroyed.
     * <p>
     * This method returns an array containing the IDs of all entities
     * that should be removed from the client's view when this packet
     * is processed. The entity IDs correspond to the unique identifiers
     * assigned to entities when they are spawned or tracked by the client.
     * </p>
     * <p>
     * The returned array should not be modified, as it may be shared
     * between multiple packet instances or used internally by the packet
     * implementation.
     * </p>
     *
     * @return an array of entity IDs to be destroyed on the client
     */
    int[] getToDestroy();
}
