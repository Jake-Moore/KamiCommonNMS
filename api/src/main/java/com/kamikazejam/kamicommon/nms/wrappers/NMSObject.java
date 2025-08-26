package com.kamikazejam.kamicommon.nms.wrappers;

import org.jetbrains.annotations.NotNull;

/**
 * Base interface for all NMS (Net Minecraft Server) object wrappers.
 * <p>
 * This interface defines the fundamental contract for wrapper classes that
 * encapsulate NMS objects, providing a type-safe abstraction layer over
 * Minecraft's internal server objects. All NMS wrappers must provide access
 * to their underlying NMS handle for direct manipulation when needed.
 * </p>
 * <p>
 * The wrapper pattern is used throughout the NMS abstraction layer to:
 * <ul>
 * <li>Provide version-independent interfaces for NMS functionality</li>
 * <li>Hide complex reflection-based access patterns</li>
 * <li>Enable type-safe operations on NMS objects</li>
 * <li>Maintain compatibility across different Minecraft versions</li>
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Wrapper provides safe, abstracted access
 * NMSChunk chunk = chunkProvider.wrap(bukkitChunk);
 * 
 * // Direct handle access when needed for advanced operations
 * Object nmsChunkHandle = chunk.getHandle();
 * }</pre>
 * </p>
 */
public interface NMSObject {
    /**
     * Retrieves the underlying NMS object handle.
     * <p>
     * This method provides access to the raw NMS object that this wrapper
     * encapsulates. The returned object is the actual Minecraft server
     * implementation instance, typically obtained through reflection or
     * version-specific factories.
     * </p>
     * <p>
     * <strong>Warning:</strong> Direct manipulation of NMS handles should
     * be used sparingly and with caution, as it bypasses the abstraction
     * layer's version compatibility guarantees. Prefer using the wrapper's
     * provided methods when possible.
     * </p>
     *
     * @return the underlying NMS object instance
     */
    @NotNull
    Object getHandle();
}
