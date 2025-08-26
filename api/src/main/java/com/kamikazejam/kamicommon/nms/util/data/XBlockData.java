package com.kamikazejam.kamicommon.nms.util.data;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.nms.util.data.types.SlabType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

/**
 * Cross-version block data representation using XSeries for compatibility.
 * <p>
 * This class serves as a pseudo-BlockData implementation that provides
 * unified block data handling across all Minecraft versions. It combines
 * {@link XMaterialData} for basic material representation with additional
 * properties that map to 1.13+ BlockData states while maintaining
 * compatibility with pre-1.13 data values.
 * </p>
 * <p>
 * The 1.13 "flattening" update introduced the BlockData system, replacing
 * the old material ID + data value system with rich block state properties.
 * This class bridges that gap by storing both the legacy material data and
 * modern block state properties, allowing block utilities to handle both
 * systems appropriately.
 * </p>
 * <p>
 * Block utilities (particularly {@code IBlockUtil1_13}) use the properties
 * defined in this class to set appropriate block states when placing blocks,
 * ensuring consistent behavior across all supported Minecraft versions.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create basic block data
 * XBlockData stone = new XBlockData(XMaterial.STONE);
 * 
 * // Create with specific properties
 * XBlockData upperSlab = new XBlockData(XMaterial.STONE_SLAB)
 *     .setSlabType(SlabType.TOP);
 * 
 * // Create water with specific level
 * XBlockData water = new XBlockData(XMaterial.WATER)
 *     .setLevel(8);
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
@Getter
@Accessors(chain = true)
public class XBlockData {
    /**
     * The underlying material data containing the base material and legacy data value.
     * <p>
     * This {@link XMaterialData} provides the foundation for block identification,
     * containing both the {@link XMaterial} and any legacy data value needed
     * for pre-1.13 compatibility.
     * </p>
     */
    private final XMaterialData materialData;
    
    /**
     * The fluid level for liquid blocks (water, lava).
     * <p>
     * In pre-1.13 versions, fluid levels were controlled through data values.
     * In 1.13+ versions, this maps to the {@code level} block state property.
     * When set, this value takes precedence over the material data value for
     * fluid level determination.
     * </p>
     * <p>
     * Valid values are typically 0-15, where 0 represents a full source block
     * and higher values represent flowing fluids with decreasing levels.
     * Level 8 often represents "stationary" fluids in pre-1.13 systems.
     * </p>
     */
    @Setter private @Nullable Integer level = null;
    
    /**
     * The slab type for slab blocks.
     * <p>
     * In pre-1.13 versions, slab positioning was controlled through data values
     * (0-7 for bottom, 8+ for top). In 1.13+ versions, this maps to the
     * {@code type} block state property. When set, this value determines
     * how the slab should be positioned within the block space.
     * </p>
     */
    @Setter private @Nullable SlabType slabType = null;

    /**
     * Creates block data from an {@link XMaterial} with default properties.
     * <p>
     * This constructor creates an {@link XMaterialData} using the material's
     * default data value, suitable for most basic block placement operations.
     * </p>
     *
     * @param material the {@link XMaterial} to use for this block data
     */
    public XBlockData(XMaterial material) {
        this.materialData = new XMaterialData(material);
    }

    /**
     * Creates block data from an {@link XMaterial} with a specific data value.
     * <p>
     * This constructor allows specification of a custom data value, which is
     * particularly useful for pre-1.13 compatibility where data values
     * determine block variants and states.
     * </p>
     *
     * @param material the {@link XMaterial} to use for this block data
     * @param data the specific data value to use
     */
    public XBlockData(XMaterial material, int data) {
        this.materialData = new XMaterialData(material, data);
    }

    /**
     * Creates block data from existing {@link XMaterialData}.
     * <p>
     * This constructor accepts pre-configured material data, allowing
     * for more complex initialization scenarios or reuse of existing
     * material data instances.
     * </p>
     *
     * @param materialData the {@link XMaterialData} to use for this block data
     */
    public XBlockData(XMaterialData materialData) {
        this.materialData = materialData;
    }


    // ---------------------------------------------------------- //
    //           Static instances for common BlockData            //
    // ---------------------------------------------------------- //

    /**
     * Pre-configured block data for source water blocks.
     * <p>
     * Represents a full water source block, equivalent to placing water
     * from a bucket. Uses the default data value for {@link XMaterial#WATER}.
     * </p>
     */
    public static XBlockData SOURCE_WATER = new XBlockData(XMaterial.WATER);
    
    /**
     * Pre-configured block data for stationary water.
     * <p>
     * Represents stationary water with data value 8 for pre-1.13 compatibility
     * and level 8 for 1.13+ versions. This typically represents water that
     * doesn't flow or update.
     * </p>
     */
    public static XBlockData STATIONARY_WATER = new XBlockData(XMaterial.WATER, 8).setLevel(8);
    
    /**
     * Pre-configured block data for upper stone brick slabs.
     * <p>
     * Represents a stone brick slab positioned at the top of a block space.
     * Uses data value 8 for pre-1.13 compatibility and {@link SlabType#TOP}
     * for 1.13+ versions.
     * </p>
     */
    public static XBlockData UPPER_STONE_SLAB = new XBlockData(XMaterial.STONE_BRICK_SLAB, 8).setSlabType(SlabType.TOP);
    
    /**
     * Pre-configured block data for red sand blocks.
     * <p>
     * Represents red sand, which was {@code SAND:1} in pre-1.13 versions
     * and became {@link XMaterial#RED_SAND} in 1.13+ versions. The
     * {@link XMaterial} system handles this transition automatically.
     * </p>
     */
    public static XBlockData RED_SAND = new XBlockData(XMaterial.RED_SAND);
}
