package com.kamikazejam.kamicommon.nms.text;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves one {@link TextBundle} per shaded-tier module, out of the nested jar.
 *
 * <p>The counterpart to {@code NmsBundles}, and separate from it on purpose. {@code NmsBundles}
 * loads adapters from the plugin classloader; these come from the CHILD loader that holds the
 * relocated Adventure, because they are the implementations that name it.
 *
 * <p>Only the tiers that use the relocated copy go through here, meaning servers below 1.21.4. From
 * 1.21.4 upward the server has Adventure natively and {@code v1_21_4} implements text directly with
 * no indirection.
 */
@ApiStatus.Internal
public final class TextBundles {

    private static final Map<String, TextBundle> CACHE = new HashMap<String, TextBundle>();

    private static final String PACKAGE = "com.kamikazejam.kamicommon.nms.text.TextBundleImpl_";

    private TextBundles() {}

    /**
     * @param module the Gradle module name whose text implementation is wanted, e.g. {@code v1_17_R1}
     * @throws IllegalStateException if the nested jar is missing or has no implementation for it
     */
    public static synchronized @NotNull TextBundle forModule(@NotNull String module) {
        TextBundle bundle = CACHE.get(module);
        if (bundle == null) {
            bundle = load(module);
            CACHE.put(module, bundle);
        }
        return bundle;
    }

    private static @NotNull TextBundle load(@NotNull String module) {
        String className = PACKAGE + module;
        try {
            Class<?> type = Class.forName(className, true, ShimLoader.get());
            return (TextBundle) type.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "No text implementation " + className + " in the bundled Adventure jar. Either "
                            + "the nested jar was stripped from this build, or a dispatch table names "
                            + "a module with no text support.", e);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not instantiate " + className, e);
        }
    }
}
