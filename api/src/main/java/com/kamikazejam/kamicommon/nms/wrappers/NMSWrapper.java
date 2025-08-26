package com.kamikazejam.kamicommon.nms.wrappers;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.util.nms.NmsVersionParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for version-specific NMS wrapper factories.
 * <p>
 * This class extends the provider pattern to support NMS wrapper creation
 * that depends on both the current Minecraft version and a context object.
 * It provides lazy initialization of wrappers based on the current NMS version,
 * while accepting an additional parameter for wrapper creation.
 * </p>
 * <p>
 * Unlike the simpler {@code Provider<T>} pattern, this wrapper factory pattern
 * is designed for scenarios where the wrapper creation requires external context,
 * such as wrapping existing Bukkit objects into their NMS equivalents or
 * creating wrappers that depend on world state or configuration.
 * </p>
 * <p>
 * The wrapper is cached after first creation to avoid repeated version checks
 * and reflection operations, improving performance for subsequent access.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * public class ChunkWrapper extends NMSWrapper<NMSChunk, Chunk> {
 *     @Override
 *     protected NMSChunk provide(int formattedNmsInteger, Chunk bukkitChunk) {
 *         if (formattedNmsInteger >= f("1.17")) {
 *             return new NMSChunk1_17(bukkitChunk);
 *         } else if (formattedNmsInteger >= f("1.13")) {
 *             return new NMSChunk1_13(bukkitChunk);
 *         } else {
 *             return new NMSChunkLegacy(bukkitChunk);
 *         }
 *     }
 * }
 * }</pre>
 * </p>
 *
 * @param <A> the type of NMS wrapper to create
 * @param <B> the type of context object required for wrapper creation
 */
@SuppressWarnings("unused")
public abstract class NMSWrapper<A, B> {

    private @Nullable A value = null;

    /**
     * Retrieves the version-specific NMS wrapper, creating it if necessary.
     * <p>
     * This method uses lazy initialization to create the wrapper on first
     * access using the provided context object. Subsequent calls return
     * the cached instance. The wrapper implementation is determined by
     * calling {@link #provide(int, Object)} with the current NMS version
     * and the provided context.
     * </p>
     * <p>
     * <strong>Note:</strong> The wrapper is cached after first creation,
     * so the context object is only used during initial creation. If you
     * need different wrappers for different contexts, use separate wrapper
     * instances.
     * </p>
     *
     * @param b the context object required for wrapper creation
     * @return the version-specific NMS wrapper instance
     */
    public final @NotNull A get(@NotNull B b) {
        if (value == null) {
            value = provide(NmsVersion.getFormattedNmsInteger(), b);
        }
        return value;
    }

    /**
     * Creates the appropriate NMS wrapper for the specified version and context.
     * <p>
     * This method must be implemented by subclasses to provide version-specific
     * logic for creating the appropriate wrapper implementation. The method
     * receives both a formatted NMS integer for version comparison and a
     * context object that provides necessary information for wrapper creation.
     * </p>
     * <p>
     * Use the {@link #f(String)} helper method to convert Minecraft version
     * strings to formatted integers for comparison.
     * </p>
     *
     * @param formattedNmsInteger the formatted NMS version integer from {@link NmsVersion#getFormattedNmsInteger()}
     * @param b the context object containing information needed for wrapper creation
     * @return the wrapper instance appropriate for the given version and context
     */
    protected abstract @NotNull A provide(int formattedNmsInteger, @NotNull B b);

    /**
     * Convenience method to convert a Minecraft version string to a formatted integer.
     * <p>
     * This helper method converts a Minecraft version string (e.g., "1.17.1", "1.13.2")
     * to a formatted integer that can be used for version comparisons in the
     * {@link #provide(int, Object)} method.
     * </p>
     * <p>
     * This method is primarily intended for use within {@link #provide(int, Object)}
     * implementations to create readable version threshold comparisons.
     * </p>
     *
     * @param mcVersion the Minecraft version string to convert (e.g., "1.17.1")
     * @return the formatted integer representation of the version
     */
    public final int f(String mcVersion) {
        return NmsVersionParser.getFormattedNmsInteger(mcVersion);
    }
}
