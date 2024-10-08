package com.kamikazejam.kamicommon.nms.util.data;

import com.cryptomorin.xseries.XMaterial;
import lombok.Data;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated Use {@link XMaterialData} instead.
 */
@SuppressWarnings("unused")
@Data @Deprecated
public class MaterialData {
    private final Material material;
    private final byte data;

    public MaterialData(Material material) {
        this.material = material;
        this.data = 0;
    }

    public MaterialData(Material material, byte data) {
        this.material = material;
        this.data = data;
    }

    public MaterialData(XMaterial xMaterial) {
        this.material = xMaterial.parseMaterial();
        this.data = xMaterial.getData();
    }

    public MaterialData(XMaterial xMaterial, byte data) {
        this.material = xMaterial.parseMaterial();
        this.data = data;
    }

    public org.bukkit.material.MaterialData toMaterialData() {
        return new org.bukkit.material.MaterialData(material, data);
    }

    public @NotNull XMaterial getXMaterialFromMaterial() {
        return XMaterial.matchXMaterial(material);
    }
}
