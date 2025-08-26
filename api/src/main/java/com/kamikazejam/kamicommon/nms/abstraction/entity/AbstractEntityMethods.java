package com.kamikazejam.kamicommon.nms.abstraction.entity;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Abstract class providing version-specific implementations for entity manipulation
 * and spawner item handling across different Minecraft versions.
 * <p>
 * This abstraction handles various entity-related operations that may differ
 * between Minecraft versions, including entity dimensions, AI controls,
 * persistence settings, and spawner item manipulation.
 * </p>
 */
@SuppressWarnings("unused")
public abstract class AbstractEntityMethods {

    /**
     * Retrieves the height of the specified entity.
     * <p>
     * This method returns the entity's bounding box height, which represents
     * the vertical space the entity occupies in the world. This is useful
     * for collision detection, spawning calculations, and space validation.
     * </p>
     *
     * @param entity the {@link Entity} to get the height of
     * @return the height of the entity in blocks
     */
    public abstract double getEntityHeight(@NotNull Entity entity);

    /**
     * Retrieves the width of the specified entity.
     * <p>
     * This method returns the entity's bounding box width, which represents
     * the horizontal space the entity occupies in the world. This is useful
     * for collision detection, spawning calculations, and space validation.
     * </p>
     *
     * @param entity the {@link Entity} to get the width of
     * @return the width of the entity in blocks
     */
    public abstract double getEntityWidth(@NotNull Entity entity);

    /**
     * Sets whether the creature should persist (not despawn naturally).
     * <p>
     * When set to {@code true}, the creature will not despawn due to
     * distance from players or other natural despawning conditions.
     * This is useful for important NPCs or entities that should remain
     * in the world permanently.
     * </p>
     *
     * @param creature the {@link Creature} to modify
     * @param value {@code true} to make the creature persist,
     *              {@code false} to allow natural despawning
     */
    public abstract void setPersists(@NotNull Creature creature, boolean value);

    /**
     * Sets whether the entity was spawned from a spawner block.
     * <p>
     * This flag affects various entity behaviors, including experience
     * drops and certain game mechanics. Entities spawned from spawners
     * typically have different properties than naturally spawned entities.
     * </p>
     *
     * @param entity the {@link Entity} to modify
     * @param value {@code true} to mark as spawner-spawned,
     *              {@code false} to mark as naturally spawned
     */
    public abstract void setFromSpawner(@NotNull Entity entity, boolean value);

    /**
     * Sets whether the entity should have AI enabled.
     * <p>
     * When AI is disabled, the entity will not perform any autonomous
     * behaviors such as movement, pathfinding, or attacking. The entity
     * will remain static but can still be interacted with.
     * </p>
     *
     * @param entity the {@link Entity} to modify
     * @param value {@code true} to enable AI, {@code false} to disable AI
     */
    public abstract void setMobAI(@NotNull Entity entity, boolean value);

    /**
     * Retrieves the entity type that a spawner item is configured to spawn.
     * <p>
     * This method examines a spawner {@link ItemStack} and determines what
     * type of entity it will spawn when placed. The method handles version
     * differences in how spawner data is stored.
     * </p>
     *
     * @param stack the spawner {@link ItemStack} to examine, may be {@code null}
     * @return an {@link Optional} containing the {@link EntityType} if the
     *         stack is a valid spawner, empty otherwise
     */
    public abstract @NotNull Optional<EntityType> getSpawnerType(@Nullable ItemStack stack);

    /**
     * Sets the entity type that a spawner item will spawn.
     * <p>
     * This method modifies a spawner {@link ItemStack} to spawn the specified
     * entity type when placed. The method handles version differences in how
     * spawner data is stored and applied.
     * </p>
     *
     * @param stack the spawner {@link ItemStack} to modify
     * @param type the {@link EntityType} the spawner should spawn
     * @return the modified {@link ItemStack} with the new spawner type
     */
    public abstract @NotNull ItemStack setSpawnerType(@NotNull ItemStack stack, @NotNull EntityType type);

    /**
     * Sets whether the entity should force chunk loading.
     * <p>
     * When enabled, the entity will keep its chunk loaded even when no
     * players are nearby. This is useful for entities that need to continue
     * functioning regardless of player proximity.
     * </p>
     * <p>
     * <strong>Note:</strong> This feature is only available in newer Minecraft
     * versions. On unsupported versions, this method will return {@code false}
     * and have no effect.
     * </p>
     *
     * @param entity the {@link Entity} to modify
     * @param value {@code true} to enable chunk loading, {@code false} to disable
     * @return {@code true} if this method could be applied on the server version,
     *         {@code false} if the feature is not supported
     */
    public boolean setLoadChunks(@NotNull Entity entity, boolean value) {
        // TODO, I think we can implement this in more server version 1.8+
        return false;
    }
}
