package com.kamikazejam.kamicommon.nms.bundle;

import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.nms.text.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * The one entry point that accepts a <b>shaded</b> Adventure component, kept deliberately off
 * {@link NmsBundle}.
 * <p>
 * It lives on its own interface with its own per-module implementation because of how the JVM
 * resolves overrides. {@link NmsBundles} loads a module's {@code NmsBundleImpl} with
 * {@code Class.forName}, and verifying that class checks each override against the interface method
 * it implements, which <b>loads the parameter types</b>. While {@code componentFrom(Component)} was
 * declared on {@code NmsBundleImpl}, loading <i>any</i> capability from a module dragged the shaded
 * Adventure copy in with it.
 * </p>
 * <p>
 * That was not theoretical. On a Paper 26.2 server, {@code commandMapModifier} dispatches to
 * {@code v1_17_R1}; loading that adapter resolved the shaded {@code Component} and its six
 * supertypes, on a server that has Adventure natively and touches no shaded text at all. Measured by
 * booting 26.2 with the shaded package deleted from the jar:
 * {@code NoClassDefFoundError ... at NmsBundles.load ... at CommandMapModifierProvider.provide}.
 * </p>
 * <p>
 * Splitting it out means the shaded copy is loaded only when a caller actually asks for a shaded
 * component, which is what {@code VersionedComponentSerializer#fromInternalComponent} and
 * {@code VersionedComponent#asInternalComponent} exist for. Declaring the type in a method
 * <i>descriptor</i>, as this interface and {@link NmsBundle} both still do, does not load it; only
 * an override in a loaded class does.
 * </p>
 */
public interface ShadedComponentBridge {

    /**
     * Wraps a shaded Adventure component for this server version.
     *
     * @param component a component from the relocated Adventure copy bundled with this library
     */
    @NotNull VersionedComponent componentFrom(@NotNull Component component);
}
