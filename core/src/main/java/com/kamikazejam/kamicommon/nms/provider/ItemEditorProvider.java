package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import com.kamikazejam.kamicommon.nms.item.ItemEditor_1_11_R1;
import com.kamikazejam.kamicommon.nms.item.ItemEditor_LATEST;
import com.kamikazejam.kamicommon.nms.item.ItemEditor_1_8_R1;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific item editor implementations.
 * <p>
 * This provider selects the appropriate {@link AbstractItemEditor} implementation
 * based on the current Minecraft version, handling the introduction of new item
 * properties and metadata systems. Item editors provide advanced ItemMeta
 * manipulation capabilities that extend beyond standard Bukkit API functionality.
 * </p>
 * <p>
 * The provider addresses the introduction of new item features:
 * <ul>
 * <li><strong>1.8-1.10:</strong> Basic item metadata with limited unbreakable support</li>
 * <li><strong>1.11+:</strong> Enhanced item properties including proper unbreakable flags</li>
 * </ul>
 * </p>
 * <p>
 * This enables consistent item manipulation across versions while providing access
 * to advanced features like unbreakable items, custom attributes, and other
 * metadata properties that may not be fully supported by the standard Bukkit API.
 * </p>
 *
 * @see AbstractItemEditor
 */
public class ItemEditorProvider extends Provider<AbstractItemEditor> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the item editor implementation based on the introduction of enhanced
     * item properties in Minecraft 1.11. The selection ensures access to the most
     * appropriate item manipulation methods for each version.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractItemEditor} implementation
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
    @Override
    protected @NotNull AbstractItemEditor provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver < f("1.11")) {
            // Pre 1.11 versions (1.8, 1.9, 1.10)
            return new ItemEditor_1_8_R1();
        }

        if (ver < f("1.13")) {
            // Pre 1.13 versions (1.11, 1.12)
            return new ItemEditor_1_11_R1();
        }

        // 1.13+ versions
        return new ItemEditor_LATEST();
    }
}
