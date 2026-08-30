package com.kamikazejam.kamicommon.nms.text;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Loads the relocated Adventure copy from {@code internal-libs/adventure.jar} inside this jar, in a
 * CHILD classloader.
 *
 * <p><b>Why a nested jar rather than shading it flat.</b> The relocated Adventure exists only for
 * servers with no native Adventure, meaning everything below 1.18.2. Shading it as ordinary class
 * entries made it importable by every consumer, and dependency scoping cannot fix that: measured
 * 2026-08-30, a {@code spigot-jar} consumer compiled against the shaded Adventure successfully even
 * with the dependency declared runtime-only, because scope metadata cannot hide bytes that are
 * physically present. Java's classpath has no nested-jar support, so entries inside this jar are not
 * classpath entries and {@code javac} cannot reach them. That survives a consumer shading this
 * library, which is the property scoping could not provide.
 *
 * <p><b>Why a child loader rather than adding it to the plugin's own.</b> Injecting into the plugin
 * classloader needs {@code URLClassLoader.addURL}, and {@code setAccessible} on it fails on Java 16
 * and up. The only route that still works reaches {@code MethodHandles.Lookup.IMPL_LOOKUP} through
 * {@code sun.misc.Unsafe}, which Java 25 already warns is scheduled for removal. A child loader uses
 * only {@code URLClassLoader} and parent delegation, neither deprecated.
 *
 * <p><b>How the boundary works.</b> Delegation is parent-first, so the child sees everything the
 * plugin classloader has and can implement {@link VersionedComponent}, whose {@code Class} object it
 * resolves by delegating upward. The parent cannot see into the child, so no plugin, no consumer and
 * no downstream plugin can reach the relocated Adventure. Instances cross the boundary as
 * {@code VersionedComponent}, which names nothing relocated.
 *
 * <p>Verified on 1.8.8, 1.16.5, 1.20.4, 1.21.11 and 26.2, across Java 8, 17, 21 and 25, including a
 * three-plugin topology where one plugin shades this library, a second depends on that plugin, and a
 * third shades its own copy.
 */
@ApiStatus.Internal
public final class ShimLoader {

    private static final String NESTED = "internal-libs/adventure.jar";

    private static volatile ClassLoader loader;
    private static volatile File cacheDir;

    private ShimLoader() {}

    /**
     * Where to extract the nested jar. Call this before anything touches text, from the plugin's
     * {@code onEnable}, passing the plugin's data folder.
     *
     * <p>The default is the system temp directory, which works on a developer machine and is the
     * wrong answer on hosted servers, where temp is routinely mounted noexec or read-only. The
     * failure that produces does not look like it has anything to do with Adventure.
     */
    public static void configure(@NotNull File dataFolder) {
        cacheDir = new File(dataFolder, "internal");
    }

    /** The loader, extracting into whatever {@link #configure(File)} was given, or temp. */
    public static @NotNull ClassLoader get() {
        File dir = cacheDir;
        if (dir == null) {
            dir = new File(System.getProperty("java.io.tmpdir"), "kamicommon-nms");
        }
        return get(dir);
    }

    /**
     * The classloader holding the relocated Adventure and the implementations that use it.
     *
     * @param cacheDir where to place the extracted jar. Use the plugin's data folder rather than the
     *                 system temp directory: hosted servers routinely mount temp noexec or read-only,
     *                 and the failure that produces is not obviously about Adventure at all.
     */
    public static @NotNull ClassLoader get(@NotNull File cacheDir) {
        ClassLoader local = loader;
        if (local != null) { return local; }
        synchronized (ShimLoader.class) {
            if (loader != null) { return loader; }
            loader = create(cacheDir);
            return loader;
        }
    }

    private static @NotNull ClassLoader create(@NotNull File cacheDir) {
        ClassLoader parent = ShimLoader.class.getClassLoader();
        try {
            if (!cacheDir.isDirectory() && !cacheDir.mkdirs()) {
                throw new IllegalStateException("could not create " + cacheDir);
            }
            File extracted = new File(cacheDir, "adventure.jar");
            // URLClassLoader cannot read a jar nested inside a jar; the JDK ships no nested-jar URL
            // handler, which is exactly why javac cannot see these classes either. Extracting is the
            // portable answer and behaves identically on 8 and 25.
            InputStream in = parent.getResourceAsStream(NESTED);
            if (in == null) {
                throw new IllegalStateException(
                        NESTED + " is missing from this jar. It carries the relocated Adventure that "
                                + "servers below 1.21.4 need. A build that strips it produces a "
                                + "library that cannot render text on those versions.");
            }
            try {
                OutputStream out = new FileOutputStream(extracted);
                try {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = in.read(buf)) > 0) { out.write(buf, 0, n); }
                } finally { out.close(); }
            } finally { in.close(); }

            return new URLClassLoader(new URL[]{ extracted.toURI().toURL() }, parent);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "could not load the bundled Adventure copy from " + NESTED, e);
        }
    }

    /**
     * Whether the relocated Adventure is reachable from the classloader that loaded this class.
     *
     * <p>It must be {@code false}. If it is true the nested jar has been unpacked into loose classes
     * somewhere in the chain and every consumer can import Adventure again.
     */
    public static boolean leakedToParent() {
        try {
            Class.forName("com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component",
                    false, ShimLoader.class.getClassLoader());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
