package com.kamikazejam.kamicommon.nms.reflection;

import java.lang.reflect.Field;

/**
 * A type-safe wrapper for reflective field access with automatic accessibility handling.
 * <p>
 * This class provides a convenient way to access private, protected, or package-private
 * fields using Java reflection. It automatically handles the accessibility setup and
 * provides type-safe operations for getting and setting field values.
 * </p>
 * <p>
 * The field handle is created once with the field name and target class, after which
 * it can be used multiple times to access the field on different instances of that class.
 * The underlying {@link Field} is made accessible during construction.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create a handle for a private field
 * FieldHandle<String> nameHandle = new FieldHandle<>("name", SomeClass.class);
 * 
 * // Get the field value from an instance
 * SomeClass instance = new SomeClass();
 * String name = nameHandle.get(instance);
 * 
 * // Set a new field value
 * nameHandle.set(instance, "New Name");
 * }</pre>
 * </p>
 *
 * @param <T> the type of the field being accessed
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class FieldHandle<T> {
    private final String fieldName;
    private final Class<?> clazz;
    private final Field field;

    /**
     * Creates a new field handle for the specified field in the given class.
     * <p>
     * This constructor uses reflection to locate the field by name and makes it
     * accessible for future operations. If the field cannot be found, a
     * {@link RuntimeException} is thrown.
     * </p>
     *
     * @param fieldName the name of the field to access
     * @param clazz the {@link Class} containing the field
     * @throws RuntimeException if the field cannot be found in the specified class
     */
    public FieldHandle(final String fieldName, final Class<?> clazz) {
        this.fieldName = fieldName;
        this.clazz = clazz;
        try {
            (this.field = clazz.getDeclaredField(fieldName)).setAccessible(true);
        } catch (final NoSuchFieldException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * Sets the value of the field on the specified object instance.
     * <p>
     * This method uses reflection to set the field value on the provided object.
     * The value is set directly without any type checking beyond what the
     * underlying reflection API provides.
     * </p>
     *
     * @param object the object instance to modify (must be an instance of the class used in construction)
     * @param value the value to set in the field
     * @throws RuntimeException if the field value cannot be set due to access restrictions or other reflection issues
     */
    public void set(final Object object, final Object value) {
        try {
            this.field.set(object, value);
        } catch (final IllegalAccessException ex) {
            throw new RuntimeException(String.format("Couldn't set value of field %s!", this.field.getName()));
        }
    }

    /**
     * Retrieves the value of the field from the specified object instance.
     * <p>
     * This method uses reflection to get the field value from the provided object.
     * The returned value is cast to the generic type {@code T} for type safety.
     * </p>
     * <p>
     * <strong>Note:</strong> The type cast is unchecked and relies on the caller
     * to ensure the generic type parameter matches the actual field type.
     * </p>
     *
     * @param object the object instance to read from (must be an instance of the class used in construction)
     * @return the current value of the field, cast to type {@code T}
     * @throws RuntimeException if the field value cannot be retrieved due to access restrictions or other reflection issues
     */
    @SuppressWarnings("unchecked")
    public T get(final Object object) {
        try {
            return (T) this.field.get(object);
        } catch (final IllegalAccessException ex) {
            throw new RuntimeException(String.format("Couldn't retrieve value of field %s!", this.field.getName()));
        }
    }
}
