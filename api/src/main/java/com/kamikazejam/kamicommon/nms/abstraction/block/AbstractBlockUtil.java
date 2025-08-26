package com.kamikazejam.kamicommon.nms.abstraction.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.nms.util.data.XBlockData;
import com.kamikazejam.kamicommon.util.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class providing version-specific implementations for high-performance
 * block manipulation operations across different Minecraft versions.
 * <p>
 * This abstraction provides optimized block placement methods that can operate
 * at different performance levels depending on the requirements. It offers
 * multiple placement strategies through the {@link PlaceType} enum, allowing
 * developers to choose between full game mechanics compliance and maximum performance.
 * </p>
 */
@SuppressWarnings("unused")
public abstract class AbstractBlockUtil {
    // ---------------------------------------------------------------------------------------- //
    //                                     ABSTRACTION                                          //
    // ---------------------------------------------------------------------------------------- //

    /**
     * @hidden
     */
    protected abstract void setBlockInternal(@NotNull Block block, @NotNull XBlockData blockData, @NotNull PlaceType placeType);

    private void setBlockCheck(@NotNull Block block, @NotNull XBlockData blockData, @NotNull PlaceType placeType) {
        // Validate nulls
        Preconditions.checkNotNull(block, "Block cannot be null");
        Preconditions.checkNotNull(blockData, "XBlockData blockData cannot be null");
        Preconditions.checkNotNull(placeType, "PlaceType cannot be null");
        this.setBlockInternal(block, blockData, placeType);
    }


    // ---------------------------------------------------------------------------------------- //
    //                                     API METHODS                                          //
    // ---------------------------------------------------------------------------------------- //

    /**
     * Sets a block using the specified cross-version material with optimized performance.
     * <p>
     * This method provides high-performance block placement using {@link XMaterial}
     * for cross-version compatibility. The placement behavior is controlled by
     * the {@link PlaceType} parameter, allowing you to choose the appropriate
     * balance between performance and game mechanics compliance.
     * </p>
     *
     * @param block the {@link Block} to modify
     * @param xMaterial the {@link XMaterial} to set the block to
     * @param placeType the {@link PlaceType} determining placement behavior
     */
    public final void setBlockSuperFast(@NotNull Block block, @NotNull XMaterial xMaterial, @NotNull PlaceType placeType) {
        this.setBlockCheck(block, new XBlockData(xMaterial), placeType);
    }

    /**
     * Sets a block using the specified Bukkit material with optimized performance.
     * <p>
     * This method provides high-performance block placement using Bukkit's
     * {@link Material} enum. The material is automatically converted to
     * {@link XMaterial} for cross-version compatibility before placement.
     * </p>
     *
     * @param block the {@link Block} to modify
     * @param material the {@link Material} to set the block to
     * @param placeType the {@link PlaceType} determining placement behavior
     */
    public final void setBlockSuperFast(@NotNull Block block, @NotNull Material material, @NotNull PlaceType placeType) {
        this.setBlockCheck(block, new XBlockData(XMaterial.matchXMaterial(material)), placeType);
    }

    /**
     * Sets a block using the specified block data with optimized performance.
     * <p>
     * This method provides high-performance block placement using pre-configured
     * {@link XBlockData}. This is the most direct approach when you already have
     * the block data prepared and want maximum control over the placement operation.
     * </p>
     *
     * @param block the {@link Block} to modify
     * @param blockData the {@link XBlockData} containing the block information
     * @param placeType the {@link PlaceType} determining placement behavior
     */
    public final void setBlockSuperFast(@NotNull Block block, @NotNull XBlockData blockData, @NotNull PlaceType placeType) {
        this.setBlockCheck(block, blockData, placeType);
    }

    /**
     * Sets a block at the specified location using cross-version material with optimized performance.
     * <p>
     * This convenience method extracts the block from the provided location and
     * sets it using the specified {@link XMaterial}. It provides the same
     * performance characteristics as the block-based method while offering
     * location-based convenience.
     * </p>
     *
     * @param location the {@link Location} of the block to modify
     * @param xMaterial the {@link XMaterial} to set the block to
     * @param placeType the {@link PlaceType} determining placement behavior
     */
    public final void setBlockSuperFast(@NotNull Location location, @NotNull XMaterial xMaterial, @NotNull PlaceType placeType) {
        this.setBlockCheck(location.getBlock(), new XBlockData(xMaterial), placeType);
    }

    /**
     * Sets a block at the specified location using Bukkit material with optimized performance.
     * <p>
     * This convenience method extracts the block from the provided location and
     * sets it using the specified {@link Material}. The material is automatically
     * converted for cross-version compatibility before placement.
     * </p>
     *
     * @param location the {@link Location} of the block to modify
     * @param material the {@link Material} to set the block to
     * @param placeType the {@link PlaceType} determining placement behavior
     */
    public final void setBlockSuperFast(@NotNull Location location, @NotNull Material material, @NotNull PlaceType placeType) {
        this.setBlockCheck(location.getBlock(), new XBlockData(XMaterial.matchXMaterial(material)), placeType);
    }

    /**
     * Sets a block at the specified location using block data with optimized performance.
     * <p>
     * This convenience method extracts the block from the provided location and
     * sets it using the specified {@link XBlockData}. It provides location-based
     * convenience while maintaining the performance benefits of pre-configured block data.
     * </p>
     *
     * @param location the {@link Location} of the block to modify
     * @param blockData the {@link XBlockData} containing the block information
     * @param placeType the {@link PlaceType} determining placement behavior
     */
    public final void setBlockSuperFast(@NotNull Location location, @NotNull XBlockData blockData, @NotNull PlaceType placeType) {
        this.setBlockCheck(location.getBlock(), blockData, placeType);
    }


    // ---------------------------------------------------------------------------------------- //
    //                                    UTIL METHODS                                          //
    // ---------------------------------------------------------------------------------------- //

    /**
     * @hidden
     * Always available pre v1.13
     */
    @SuppressWarnings("deprecation")
    @ApiStatus.Internal
    protected final int legacyGetCombined(XMaterial xMaterial) {
        assert xMaterial.parseMaterial() != null;
        return this.legacyGetCombined(xMaterial.parseMaterial().getId(), xMaterial.getData());
    }

    /**
     * @hidden
     */
    @SuppressWarnings("deprecation")
    @ApiStatus.Internal
    protected final int legacyGetCombined(Material material, byte data) {
        return this.legacyGetCombined(material.getId(), data);
    }

    /**
     * @hidden
     */
    @ApiStatus.Internal
    protected final int legacyGetCombined(int id, byte data) {
        return id + (data << 12);
    }
}
