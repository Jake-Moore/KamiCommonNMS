package com.kamikazejam.kamicommon.nms.wrappers.packet.impl;

import com.kamikazejam.kamicommon.nms.wrappers.packet.NMSPacket;

/**
 * NMS wrapper for outbound entity status packets.
 * <p>
 * This interface provides access to Minecraft's entity status packet,
 * which is sent from the server to clients to notify them of state changes
 * or events related to specific entities. Entity status packets are used
 * to trigger visual or audio effects on the client side without requiring
 * full entity updates.
 * </p>
 * <p>
 * The status packet contains an entity ID and a status byte that represents
 * various entity states or events. Common status values include death animations,
 * damage effects, potion effects, or other entity-specific events that need
 * to be synchronized with the client.
 * </p>
 * <p>
 * Status values vary by entity type and Minecraft version, but some common examples include:
 * <ul>
 * <li>Status 2: Entity hurt/damage animation</li>
 * <li>Status 3: Entity death animation</li>
 * <li>Status 6-9: Wolf states (angry, sitting, etc.)</li>
 * <li>Status 10: Sheep eating grass</li>
 * <li>Status 18: Rabbit jumping</li>
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Read entity status from a packet
 * int entityId = statusPacket.getEntityID();
 * byte status = statusPacket.getStatus();
 * 
 * // Handle specific status events
 * if (status == 3) {
 *     // Entity death animation
 *     handleEntityDeath(entityId);
 * }
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
public interface NMSOutEntityStatus extends NMSPacket {

    /**
     * Retrieves the ID of the entity this status update applies to.
     * <p>
     * This method returns the unique identifier of the entity that is
     * experiencing the status change. The entity ID corresponds to the
     * same identifier used when the entity was spawned and is tracked
     * by the client for entity management.
     * </p>
     *
     * @return the entity ID for this status update
     */
    int getEntityID();

    /**
     * Retrieves the status byte indicating the type of entity event or state.
     * <p>
     * This method returns a byte value that represents the specific status
     * or event that occurred for the entity. The meaning of status values
     * varies by entity type and Minecraft version, but they typically
     * correspond to visual effects, animations, or state changes.
     * </p>
     * <p>
     * Common status values include damage effects, death animations,
     * entity-specific behaviors, and other events that need to be
     * synchronized between server and client.
     * </p>
     *
     * @return the status byte representing the entity event or state
     */
    byte getStatus();
}
