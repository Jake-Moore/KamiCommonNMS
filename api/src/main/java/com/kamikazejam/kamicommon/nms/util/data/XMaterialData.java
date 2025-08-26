package com.kamikazejam.kamicommon.nms.util.data;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * Cross-version material data wrapper using XSeries for compatibility.
 * <p>
 * This class serves as a pseudo-MaterialData implementation that combines
 * {@link XMaterial} with legacy data values to provide consistent material
 * representation across all Minecraft versions. It bridges the gap between
 * pre-1.13 material data values and 1.13+ flattened materials.
 * </p>
 * <p>
 * In pre-1.13 versions, blocks were identified by a material ID and a data value
 * (0-15) that specified variants like color, orientation, or state. The 1.13
 * "flattening" update replaced this system with individual materials for each
 * variant. This class abstracts those differences by storing both the
 * {@link XMaterial} and the legacy data value when needed.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create with default data value from XMaterial
 * XMaterialData data = new XMaterialData(XMaterial.STONE_BRICKS);
 * 
 * // Create with specific data value for pre-1.13 compatibility
 * XMaterialData redWool = new XMaterialData(XMaterial.RED_WOOL, 14);
 * }</pre>
 * </p>
 */
@SuppressWarnings("unused")
@Getter
@Setter
@Accessors(chain = true)
public class XMaterialData {
    /**
     * The legacy data value used for pre-1.13 material variants.
     * <p>
     * This byte value (0-15) was used in pre-1.13 versions to specify
     * material variants such as wool colors, wood types, or block orientations.
     * For 1.13+ materials, this value may be ignored or used for internal
     * compatibility purposes.
     * </p>
     */
    private byte data;
    
    /**
     * The cross-version material representation.
     * <p>
     * This {@link XMaterial} provides a unified interface for accessing
     * materials across all Minecraft versions, automatically handling
     * the differences between pre-1.13 and post-1.13 material systems.
     * </p>
     */
    private @NotNull XMaterial material;

    /**
     * Creates material data with the default data value from {@link XMaterial}.
     * <p>
     * This constructor uses the default data value provided by the {@link XMaterial}
     * instance, which is typically the most appropriate value for representing
     * that material in pre-1.13 versions.
     * </p>
     *
     * @param material the {@link XMaterial} to wrap
     */
    public XMaterialData(@NotNull XMaterial material) {
        // Default to whatever the XMaterial data value needs to set that block
        this.material = material;
        this.data = material.getData();
    }

    /**
     * Creates material data with a specific data value.
     * <p>
     * This constructor allows explicit specification of the data value,
     * which is useful when you need a specific variant that differs from
     * the {@link XMaterial}'s default data value. This is particularly
     * important for pre-1.13 compatibility where data values determine
     * material variants.
     * </p>
     *
     * @param material the {@link XMaterial} to wrap
     * @param data the specific data value to use (will be cast to byte)
     */
    public XMaterialData(@NotNull XMaterial material, int data) {
        // Use the specified data value
        this.material = material;
        this.data = (byte) data;
    }
}
