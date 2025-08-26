package com.kamikazejam.kamicommon.nms.util.data.types;

/**
 * Cross-version enumeration for slab placement types.
 * <p>
 * This enum replicates the functionality of the 1.13+ {@code Slab.Type} enum
 * to provide consistent slab type representation across all Minecraft versions.
 * It defines the three possible positions a slab can occupy within a block space.
 * </p>
 * <p>
 * In pre-1.13 versions, slab positioning was controlled through material data values,
 * while 1.13+ uses proper block state properties. This enum abstracts away those
 * implementation differences and provides a unified interface for slab positioning.
 * </p>
 */
public enum SlabType {
    /**
     * Represents a slab positioned at the top half of a block.
     * <p>
     * In pre-1.13 versions, this corresponds to slab materials with data value 8+.
     * In 1.13+ versions, this maps to the {@code type=top} block state property.
     * </p>
     */
    TOP,
    
    /**
     * Represents a slab positioned at the bottom half of a block.
     * <p>
     * In pre-1.13 versions, this corresponds to slab materials with data value 0-7.
     * In 1.13+ versions, this maps to the {@code type=bottom} block state property.
     * </p>
     */
    BOTTOM,
    
    /**
     * Represents a double slab that occupies the entire block space.
     * <p>
     * Double slabs are created when two slabs of the same type are placed in
     * the same block space. In pre-1.13 versions, this typically corresponds
     * to separate "double slab" materials. In 1.13+ versions, this maps to
     * the {@code type=double} block state property.
     * </p>
     */
    DOUBLE
}
