package com.kamikazejam.kamicommon.nms.reflection;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Factory and cache manager for {@link FieldHandle} instances.
 * <p>
 * This utility class provides a centralized way to create and cache {@link FieldHandle}
 * instances to avoid the overhead of repeated reflection operations. Each unique
 * combination of field name and class is cached, so subsequent requests for the
 * same field handle return the cached instance.
 * </p>
 * <p>
 * The caching is based on a serialized key that combines the field name and the
 * fully qualified class name, ensuring that field handles for fields with the
 * same name in different classes are properly distinguished.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Get a cached field handle
 * FieldHandle<?> handle = FieldHandles.getHandle("myField", MyClass.class);
 * 
 * // Use the handle
 * Object value = handle.get(someInstance);
 * handle.set(someInstance, newValue);
 * }</pre>
 * </p>
 */
public class FieldHandles {
    private static final Map<String, FieldHandle<?>> handleCache;

    static {
        handleCache = Maps.newHashMap();
    }

    /**
     * Creates a serialized key for caching field handles.
     * <p>
     * This method generates a unique string key that combines the field name
     * and the fully qualified class name. This ensures that fields with the
     * same name in different classes are treated as separate cache entries.
     * </p>
     *
     * @param fieldName the name of the field
     * @param clazz the class containing the field
     * @return a unique string key for cache lookup
     */
    private static String getSerializedName(final String fieldName, final Class<?> clazz) {
        return String.format("%s+%s", fieldName, clazz.getName());
    }

    /**
     * Retrieves a cached {@link FieldHandle} or creates a new one if not cached.
     * <p>
     * This method first checks the cache for an existing field handle with the
     * specified field name and class. If found, the cached instance is returned.
     * If not found, a new {@link FieldHandle} is created, cached, and returned.
     * </p>
     * <p>
     * This approach ensures that the reflection setup cost is only paid once
     * per unique field, improving performance for repeated field access operations.
     * </p>
     *
     * @param fieldName the name of the field to access
     * @param clazz the {@link Class} containing the field
     * @return a {@link FieldHandle} for the specified field, either from cache or newly created
     * @throws RuntimeException if the field cannot be found in the specified class (thrown by {@link FieldHandle} constructor)
     */
    public static FieldHandle<?> getHandle(final String fieldName, final Class<?> clazz) {
        final String serialized = getSerializedName(fieldName, clazz);
        if (FieldHandles.handleCache.containsKey(serialized)) {
            return FieldHandles.handleCache.get(serialized);
        }
        final FieldHandle<?> newHandle = new FieldHandle<>(fieldName, clazz);
        FieldHandles.handleCache.put(serialized, newHandle);
        return newHandle;
    }
}
