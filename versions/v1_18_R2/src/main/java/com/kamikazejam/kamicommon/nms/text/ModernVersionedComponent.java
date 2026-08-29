package com.kamikazejam.kamicommon.nms.text;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * An extension of {@link VersionedComponent} that is guaranteed to run on modern server versions that support the native adventure api.<br>
 * For server versions 1.18.2 and newer the {@link VersionedComponent} can be safely cast to this interface,
 * allowing additional native adventure functionality to be used.
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
