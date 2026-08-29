package com.kamikazejam.kamicommon.nms.text;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * An extension of {@link VersionedComponent} that is guaranteed to run on modern server versions that support the native adventure api.<br>
 * For server versions <b>1.21.4</b> and newer the {@link VersionedComponent} can be safely cast to this
 * interface, allowing additional native adventure functionality to be used.
 * <p>
 * The boundary is 1.21.4 rather than 1.18.2 because the native implementation needs
 * {@code ItemMeta.customName()}, which Paper only added in 1.21.4. Below that, dispatch returns a
 * {@code VersionedComponent_1_17_R1} backed by the shaded Adventure, which does <b>not</b> implement
 * this interface, so casting on 1.18.2 through 1.21.3 throws {@link ClassCastException}. Check with
 * {@code instanceof} rather than casting on a version assumption.
 */
@SuppressWarnings("unused")
public interface ModernVersionedComponent extends VersionedComponent {

    /**
     * Fetches (or Creates) an instance of the native adventure {@link Component} included in the paper-api<br>
     * <br>
     * Advanced users may use this in order to obtain a native component object for use with the rest of the adventure api.<br>
     */
    @NotNull Component asNativeComponent();
}
