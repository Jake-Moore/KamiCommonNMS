package com.kamikazejam.kamicommon.nms.abstraction.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom event fired before a spawner attempts to spawn an entity.
 * <p>
 * This event provides cross-version compatibility for spawner monitoring,
 * allowing plugins to intercept and potentially cancel spawner activations
 * before entities are actually created. The implementation varies based on
 * the Minecraft version and available server software.
 * </p>
 * <p>
 * <strong>Version Compatibility:</strong>
 * </p>
 * <ul>
 *   <li><strong>Pre-1.12:</strong>
 *     <ul>
 *       <li>1.8.8: Requires TacoSpigot with {@code net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent}</li>
 *       <li>Other versions: Requires Paper with {@code com.destroystokyo.paper.event.entity.SpawnerSpawnEvent}</li>
 *     </ul>
 *   </li>
 *   <li><strong>1.13+:</strong> Requires Paper with {@code com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent}</li>
 * </ul>
 * <p>
 * This event is {@link Cancellable}, allowing plugins to prevent specific
 * spawner activations while maintaining compatibility across server versions.
 * </p>
 */
@Getter
@SuppressWarnings("unused")
public class PreSpawnSpawnerEvent extends Event implements Cancellable {

    private final @NotNull Block spawnerBlock;
    private final @Nullable Location spawnLocation;
    private final @NotNull EntityType type;

    /**
     * Constructs a new PreSpawnSpawnerEvent.
     *
     * @param spawnerBlock the {@link Block} representing the spawner
     * @param type the {@link EntityType} that the spawner is attempting to spawn
     * @param spawnLocation the {@link Location} where the entity will spawn,
     *                     may be {@code null} on older versions or server software
     */
    public PreSpawnSpawnerEvent(@NotNull Block spawnerBlock, @NotNull EntityType type, @Nullable Location spawnLocation) {
        this.spawnerBlock = spawnerBlock;
        this.spawnLocation = spawnLocation;
        this.type = type;
    }

    private static final HandlerList HANDLERS = new HandlerList();
    public static HandlerList getHandlerList() {
        return PreSpawnSpawnerEvent.HANDLERS;
    }
    public HandlerList getHandlers() {
        return PreSpawnSpawnerEvent.HANDLERS;
    }

    /**
     * Gets the spawner block that is attempting to spawn an entity.
     *
     * @return the {@link Block} representing the spawner
     */
    public @NotNull Block getSpawnerBlock() {
        return spawnerBlock;
    }

    /**
     * Gets the location where the entity will spawn.
     * <p>
     * <strong>Note:</strong> This may be {@code null} on older Minecraft
     * versions or server software that doesn't provide spawn location
     * information in their spawner events.
     * </p>
     *
     * @return the spawn {@link Location}, or {@code null} if not available
     */
    public @Nullable Location getSpawnLocation() {
        return spawnLocation;
    }

    /**
     * Gets the type of entity that the spawner is attempting to spawn.
     *
     * @return the {@link EntityType} to be spawned
     */
    public @NotNull EntityType getType() {
        return type;
    }

    @Setter
    private boolean cancelled;

}

