package com.kamikazejam.kamicommon.nms.library.worldedit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Version-specific API interface for WorldEdit integration across different Minecraft versions.
 * <p>
 * This interface provides a unified abstraction layer for WorldEdit operations that may
 * vary between different WorldEdit and Minecraft versions. It handles clipboard operations,
 * schematic loading, and structure pasting with support for transformations and positioning.
 * </p>
 * <p>
 * The generic type {@code C} represents the WorldEdit clipboard type, which varies between
 * WorldEdit versions (e.g., {@code Clipboard} in newer versions vs {@code CuboidClipboard}
 * in older versions).
 * </p>
 *
 * @param <C> the WorldEdit clipboard type for the specific version being used
 */
@SuppressWarnings("unused")
public interface WorldEditApi<C> {

    /**
     * Pastes a WorldEdit clipboard into the specified world with transformations.
     * <p>
     * This method places the contents of a WorldEdit clipboard at the specified origin
     * point with optional rotation, translation, and mirroring transformations applied.
     * The paste operation respects WorldEdit's standard behavior for block placement
     * and entity handling.
     * </p>
     * <p>
     * The rotation is applied counterclockwise around the Y-axis, and offsets are
     * applied after rotation. Flipping operations are applied in the clipboard's
     * local coordinate system before rotation and translation.
     * </p>
     *
     * @param world the {@link World} to paste the clipboard into
     * @param clipboard the WorldEdit clipboard object containing the blocks to paste
     * @param origin the {@link Vector} position where the clipboard should be pasted
     * @param rotation the degree rotation to apply counterclockwise around the Y-axis
     * @param xOffset the X-axis offset to apply after rotation
     * @param yOffset the Y-axis offset to apply after rotation  
     * @param zOffset the Z-axis offset to apply after rotation
     * @param flipX whether to flip the clipboard along the X-axis before rotation
     * @param flipZ whether to flip the clipboard along the Z-axis before rotation
     * @return {@code true} if the paste operation completed without WorldEdit errors,
     *         {@code false} if WorldEdit encountered issues during pasting
     */
    boolean pasteClipboard(@NotNull World world, @NotNull C clipboard, @NotNull Vector origin, int rotation, int xOffset, int yOffset, int zOffset, boolean flipX, boolean flipZ);

    /**
     * Loads and pastes a schematic file directly to the specified location.
     * <p>
     * This convenience method loads a schematic file from disk and immediately
     * pastes it at the specified location with the given rotation. This is useful
     * for one-time paste operations where clipboard management is not needed.
     * </p>
     * <p>
     * The rotation is applied counterclockwise around the Y-axis at the paste location.
     * </p>
     *
     * @param file the schematic {@link File} to load and paste
     * @param location the {@link Location} where the schematic should be pasted
     * @param rotation the degree rotation to apply counterclockwise around the Y-axis
     * @return {@code true} if the paste operation completed without WorldEdit errors,
     *         {@code false} if WorldEdit encountered issues during loading or pasting
     * @throws NoSuchMethodException if the required WorldEdit methods are not available
     * @throws InvocationTargetException if WorldEdit method invocation fails
     * @throws IllegalAccessException if WorldEdit method access is denied
     * @throws InstantiationException if WorldEdit object instantiation fails
     */
    boolean pasteByFile(@NotNull File file, @NotNull Location location, int rotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException;

    /**
     * Loads a schematic file into a WorldEdit clipboard object.
     * <p>
     * This method loads a schematic file from disk and creates a WorldEdit clipboard
     * object that can be used for multiple paste operations or clipboard manipulation.
     * The returned clipboard contains all block data, entity data, and metadata
     * from the original schematic.
     * </p>
     * <p>
     * The world parameter is required for some WorldEdit versions to properly
     * initialize the clipboard with the correct world context.
     * </p>
     *
     * @param world the {@link World} context for loading the clipboard
     * @param file the schematic {@link File} to load into a clipboard
     * @return the loaded WorldEdit clipboard object, or {@code null} if loading failed
     */
    @Nullable
    C getClipboardByFile(@NotNull World world, @NotNull File file);

}

