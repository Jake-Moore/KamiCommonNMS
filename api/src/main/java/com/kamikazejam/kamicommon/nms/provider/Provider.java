package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for lazy-loading version-specific implementations.
 * <p>
 * This class provides a framework for creating version-specific providers that
 * determine the appropriate implementation based on the current Minecraft/NMS version.
 * The provider uses lazy initialization to defer the creation of the implementation
 * until it's actually needed, improving startup performance.
 * </p>
 * <p>
 * Subclasses must implement the {@link #provide(int)} method to return the
 * appropriate implementation for the given NMS version. The implementation
 * is cached after the first access for subsequent calls.
 * </p>
 *
 * @param <T> the type of implementation provided by this provider
 */
@SuppressWarnings("unused")
public abstract class Provider<T> {
    private @Nullable T value = null;

    /**
     * Retrieves the version-specific implementation, creating it if necessary.
     * <p>
     * This method uses lazy initialization to create the implementation on first
     * access. Subsequent calls return the cached instance. The implementation
     * is determined by calling {@link #provide(int)} with the current NMS version.
     * </p>
     *
     * @return the version-specific implementation instance
     */
    public final @NotNull T get() {
        if (value == null) {
            value = provide(NmsVersion.getFormattedNmsInteger());
        }
        return value;
    }

    /**
     * Creates the appropriate implementation for the specified NMS version.
     * <p>
     * This method must be implemented by subclasses to provide version-specific
     * logic for creating the appropriate implementation. The method receives
     * a formatted NMS integer that can be compared against known version thresholds
     * to determine which implementation to create.
     * </p>
     * <p>
     * Use the {@link #f(String)} helper method to convert Minecraft version
     * strings to formatted integers for comparison.
     * </p>
     *
     * @param formattedNmsInteger the formatted NMS version integer from {@link NmsVersion#getFormattedNmsInteger()}
     * @return the implementation instance appropriate for the given version
     */
    protected abstract @NotNull T provide(int formattedNmsInteger);

    /**
     * Convenience method to convert a Minecraft version string to a formatted integer.
     * <p>
     * This helper method converts a Minecraft version string (e.g., "1.17.1", "1.13.2")
     * to a formatted integer that can be used for version comparisons in the
     * {@link #provide(int)} method.
     * </p>
     * <p>
     * This method is primarily intended for use within {@link #provide(int)} implementations
     * to create readable version threshold comparisons.
     * </p>
     *
     * @param mcVersion the Minecraft version string to convert (e.g., "1.17.1")
     * @return the formatted integer representation of the version
     */
    public final int f(String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }
}
