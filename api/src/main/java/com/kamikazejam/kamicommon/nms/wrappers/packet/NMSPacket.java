package com.kamikazejam.kamicommon.nms.wrappers.packet;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;

/**
 * Base interface for all NMS packet wrappers.
 * <p>
 * This interface provides the foundational contract for wrapping Minecraft's
 * internal packet objects, enabling type-safe access to packet data and
 * functionality across different Minecraft versions. Packets are the fundamental
 * units of communication between the server and client in Minecraft.
 * </p>
 * <p>
 * The packet wrapper system abstracts away the complexity of version-specific
 * packet implementations while providing a unified interface for common packet
 * operations. This allows plugins to work with packets without needing to
 * handle the intricacies of different NMS packet formats.
 * </p>
 * <p>
 * Packet wrappers extend the base {@link NMSObject} contract, providing access
 * to the underlying NMS packet handle when needed for advanced operations that
 * aren't covered by the wrapper interface.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Wrap an existing packet
 * NMSPacket packet = packetHandler.wrapPacket(nmsPacketObject);
 * 
 * // Access packet-specific functionality through typed wrappers
 * if (packet instanceof NMSOutEntityDestroy) {
 *     NMSOutEntityDestroy destroyPacket = (NMSOutEntityDestroy) packet;
 *     int[] entityIds = destroyPacket.getToDestroy();
 * }
 * 
 * // Send packet to player
 * packetHandler.sendPacket(player, packet);
 * }</pre>
 * </p>
 */
public interface NMSPacket extends NMSObject {
}
