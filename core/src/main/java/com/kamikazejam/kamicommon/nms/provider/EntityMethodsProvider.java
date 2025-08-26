package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.entity.*;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific entity manipulation implementations.
 * <p>
 * This provider selects the appropriate {@link AbstractEntityMethods} implementation
 * based on the current Minecraft version, handling the evolution of entity APIs
 * and spawner mechanics across different Minecraft releases. Entity methods provide
 * advanced entity manipulation capabilities including dimension handling, AI control,
 * and spawner item management.
 * </p>
 * <p>
 * The provider covers major changes in entity handling systems:
 * <ul>
 * <li><strong>1.8-1.12:</strong> Legacy entity manipulation with NBT-based spawner items</li>
 * <li><strong>1.13+:</strong> Updated entity systems with improved spawner handling</li>
 * <li><strong>1.17+:</strong> Significant NMS restructuring affecting entity APIs</li>
 * <li><strong>1.20.5+:</strong> Mojang-mapped Paper NMS for enhanced compatibility</li>
 * </ul>
 * </p>
 * <p>
 * Each implementation provides methods for entity dimension queries, persistence
 * settings, AI control, and advanced spawner item manipulation that are essential
 * for plugins dealing with custom entities or spawner mechanics.
 * </p>
 *
 * @see AbstractEntityMethods
 */
public class EntityMethodsProvider extends Provider<AbstractEntityMethods> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the appropriate entity methods implementation based on Minecraft version,
     * accounting for significant changes in entity handling and spawner mechanics.
     * The selection ensures compatibility with both legacy NBT systems and modern
     * entity management approaches.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractEntityMethods} implementation
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
    @Override
    protected @NotNull AbstractEntityMethods provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return new EntityMethods_1_8_R1();
        }else if (ver <= f("1.8.3")) {
            return new EntityMethods_1_8_R2();
        }else if (ver <= f("1.8.8")) {
            return new EntityMethods_1_8_R3();
        }else if (ver <= f("1.9.2")) {
            return new EntityMethods_1_9_R1();
        }else if (ver <= f("1.9.4")) {
            return new EntityMethods_1_9_R2();
        }else if (ver <= f("1.10.2")) {
            return new EntityMethods_1_10_R1();
        }else if (ver <= f("1.11.2")) {
            return new EntityMethods_1_11_R1();
        }else if (ver <= f("1.12.2")) {
            return new EntityMethods_1_12_R1();
        }else if (ver <= f("1.13")) {
            return new EntityMethods_1_13_R1();
        }else if (ver <= f("1.13.2")) {
            return new EntityMethods_1_13_R2();
        }else if (ver <= f("1.14.4")) {
            return new EntityMethods_1_14_R1();
        }else if (ver <= f("1.15.2")) {
            return new EntityMethods_1_16_R1();
        }else if (ver <= f("1.16.1")) {
            return new EntityMethods_1_16_R1();
        }else if (ver <= f("1.16.3")) {
            return new EntityMethods_1_16_R2();
        }else if (ver <= f("1.16.5")) {
            return new EntityMethods_1_16_R3();
        }else if (ver <= f("1.17.1")) {
            return new EntityMethods_1_17_R1();
        }else if (ver <= f("1.18.1")) {
            return new EntityMethods_1_18_R1();
        }else if (ver <= f("1.18.2")) {
            return new EntityMethods_1_19_R1();
        }else if (ver <= f("1.19.2")) {
            return new EntityMethods_1_19_R1();
        }else if (ver <= f("1.19.3")) {
            return new EntityMethods_1_19_R2();
        }else if (ver <= f("1.19.4")) {
            return new EntityMethods_1_19_R3();
        }else if (ver <= f("1.20.1")) {
            return new EntityMethods_1_20_R1();
        }else if (ver <= f("1.20.2")) {
            return new EntityMethods_1_20_R3();
        }else if (ver <= f("1.20.4")) {
            return new EntityMethods_1_20_R3();
        }
        // With the mojang-mapped paper nms now, we might be good to use this version indefinitely
        return new EntityMethods_1_21_CB(); // Confirmed for 1.20.5, 1.20.6, 1.21
    }
}
