package com.kamikazejam.kamicommon.nms.bundle;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves one {@link NmsBundle} per {@code versions/*} module, by name, exactly once.
 * <p>
 * This is the only place in the library that loads a version-specific class reflectively, and it is
 * deliberately the only one: fifteen {@code Class.forName} call sites would be fifteen surfaces to
 * keep in sync, and this library's per-version class names are irregular enough
 * ({@code Teleporter1_8_R1}, {@code ChunkProvider_1_21_4}, {@code BlockUtil_LATEST}) that building
 * them from a version string is not safe. The adapter name is regular and derived from the Gradle
 * module name instead, and everything inside a module stays statically typed.
 * </p>
 * <p>
 * <b>Do not relocate or rename {@code com.kamikazejam.kamicommon.nms.bundle.*}.</b> The lookup is by
 * string. {@code spigot-jar} relocates only third-party packages, so KamiCommon is safe as shipped,
 * but a consumer shading this library under its own relocation rules would break it.
 * </p>
 */
public final class NmsBundles {

    /**
     * Cached one instance per module. {@link com.kamikazejam.kamicommon.nms.provider.Provider}
     * caches its own capability after the first call, so in steady state neither this map nor
     * {@link Class#forName(String)} is touched again.
     */
    private static final Map<String, NmsBundle> BUNDLES = new HashMap<String, NmsBundle>();

    /**
     * Cached separately from {@link #BUNDLES}, and loaded only when a caller actually asks for a
     * shaded Adventure component. See {@link ShadedComponentBridge} for why this cannot live on
     * {@link NmsBundle}.
     */
    private static final Map<String, ShadedComponentBridge> BRIDGES = new HashMap<String, ShadedComponentBridge>();

    private static final String PACKAGE = "com.kamikazejam.kamicommon.nms.bundle.";

    private NmsBundles() {}

    /**
     * @param module the Gradle module name, e.g. {@code v1_8_R3}, {@code v_latest}, {@code worlds7}
     * @return that module's adapter
     * @throws IllegalStateException if the module has no adapter, or if this JVM is too old to load it
     */
    public static synchronized @NotNull NmsBundle forModule(@NotNull String module) {
        NmsBundle bundle = BUNDLES.get(module);
        if (bundle == null) {
            bundle = load(module);
            BUNDLES.put(module, bundle);
        }
        return bundle;
    }

    private static @NotNull NmsBundle load(@NotNull String module) {
        String className = PACKAGE + module + ".NmsBundleImpl";
        try {
            Class<?> type = Class.forName(className);
            return (NmsBundle) type.getDeclaredConstructor().newInstance();
        } catch (UnsupportedClassVersionError e) {
            // The one failure this whole indirection exists to make legible. Without it the server
            // gets an Error out of the class loader naming a relocated class nobody recognises.
            throw new IllegalStateException(
                    "KamiCommon's '" + module + "' support module needs a newer Java version than"
                            + " this server is running (" + System.getProperty("java.version") + ")."
                            + " This is a server configuration problem, not a KamiCommon bug.", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "No NMS adapter " + className + ". Either the version module was excluded from the"
                            + " shaded jar, or a dispatch table names a module that does not exist.", e);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not instantiate " + className, e);
        }
    }

    /**
     * That module's shaded-Adventure bridge.
     * <p>
     * <b>Loading the returned class resolves the shaded Adventure copy</b>, so call this only when a
     * shaded component is genuinely wanted. Everything else should go through {@link NmsBundle}.
     * </p>
     *
     * @param module the Gradle module name, e.g. {@code v1_17_R1}
     */
    public static synchronized @NotNull ShadedComponentBridge forShadedBridge(@NotNull String module) {
        ShadedComponentBridge bridge = BRIDGES.get(module);
        if (bridge == null) {
            bridge = loadBridge(module);
            BRIDGES.put(module, bridge);
        }
        return bridge;
    }

    /**
     * The shaded bridge belonging to the same module as {@code bundle}.
     * <p>
     * Derived from the adapter's own package rather than by re-running a dispatch ladder. Running a
     * second ladder would mean two answers for the same server the moment one drifted, which this
     * project has already been bitten by. Deriving it also keeps the single ladder written as
     * {@code forModule("...")} literals, which is the form {@code verifyDispatchFloors} parses; a
     * ladder rewritten to return bare module strings would silently stop being checked.
     * </p>
     */
    public static @NotNull ShadedComponentBridge shadedBridgeFor(@NotNull NmsBundle bundle) {
        String name = bundle.getClass().getName();
        if (!name.startsWith(PACKAGE)) {
            throw new IllegalStateException(
                    "Adapter " + name + " is not under " + PACKAGE + ", so its module cannot be"
                            + " derived. Adapters must stay in their generated package.");
        }
        int end = name.lastIndexOf('.');
        int start = name.lastIndexOf('.', end - 1);
        String module = name.substring(start + 1, end);
        if (module.isEmpty()) {
            throw new IllegalStateException("Could not derive a module name from adapter " + name);
        }
        return forShadedBridge(module);
    }

    private static @NotNull ShadedComponentBridge loadBridge(@NotNull String module) {
        String className = PACKAGE + module + ".ShadedComponentBridgeImpl";
        try {
            Class<?> type = Class.forName(className);
            return (ShadedComponentBridge) type.getDeclaredConstructor().newInstance();
        } catch (UnsupportedClassVersionError e) {
            throw new IllegalStateException(
                    "KamiCommon's '" + module + "' support module needs a newer Java version than"
                            + " this server is running (" + System.getProperty("java.version") + ")."
                            + " This is a server configuration problem, not a KamiCommon bug.", e);
        } catch (NoClassDefFoundError e) {
            // Distinct from the ClassNotFoundException below: the bridge class exists but the shaded
            // Adventure it references does not. That is what happens when spigot-nms-text has been
            // excluded from a shaded jar, and it deserves to say so rather than surface a relocated
            // class name nobody recognises.
            throw new IllegalStateException(
                    "The bundled Adventure copy is missing, so shaded components are unavailable."
                            + " It ships in com.kamikazejam.kamicommon:spigot-nms-text; a shade"
                            + " configuration that excludes it breaks this path.", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "No shaded component bridge " + className + ". Either the version module was"
                            + " excluded from the shaded jar, or a dispatch table names a module that"
                            + " does not exist.", e);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not instantiate " + className, e);
        }
    }
}
