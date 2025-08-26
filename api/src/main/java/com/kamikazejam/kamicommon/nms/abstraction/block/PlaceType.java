package com.kamikazejam.kamicommon.nms.abstraction.block;

/**
 * Enumeration defining different approaches for block placement operations,
 * each with varying performance characteristics and update behaviors.
 * <p>
 * This enum allows fine-grained control over how blocks are placed in the world,
 * balancing between proper game mechanics (physics, lighting) and performance.
 * The choice of placement type depends on the specific use case and performance
 * requirements of the block placement operation.
 * </p>
 */
public enum PlaceType {

    /**
     * Standard Bukkit block placement with full physics and lighting updates.
     * <p>
     * This approach uses Bukkit's standard block placement methods, which
     * handle all physics calculations (falling blocks, water flow, redstone updates)
     * and lighting recalculations. This ensures complete game mechanics compliance
     * but is the slowest option.
     * </p>
     * <p>
     * <strong>Performance:</strong> Slowest<br>
     * <strong>Physics:</strong> Enabled<br>
     * <strong>Lighting:</strong> Enabled
     * </p>
     */
    BUKKIT,

    /**
     * Bukkit block placement with lighting updates but without physics calculations.
     * <p>
     * This approach uses Bukkit's block placement with {@code applyPhysics = false},
     * which maintains lighting updates but skips physics calculations like falling
     * blocks, water flow, and redstone updates. This provides a good balance between
     * visual correctness and performance.
     * </p>
     * <p>
     * <strong>Performance:</strong> Moderate<br>
     * <strong>Physics:</strong> Disabled<br>
     * <strong>Lighting:</strong> Enabled
     * </p>
     */
    NO_PHYSICS,

    /**
     * Direct NMS block placement with no physics or lighting updates.
     * <p>
     * This approach uses direct NMS (Net Minecraft Server) methods to place blocks
     * without any physics calculations or lighting updates. This provides the
     * maximum performance but requires manual handling of any necessary updates.
     * </p>
     * <p>
     * <strong>Performance:</strong> Fastest<br>
     * <strong>Physics:</strong> Disabled<br>
     * <strong>Lighting:</strong> Disabled
     * </p>
     * <p>
     * <strong>Warning:</strong> When using this mode, you may need to manually
     * trigger lighting updates or physics calculations if they are required
     * for your specific use case.
     * </p>
     */
    NMS
}
