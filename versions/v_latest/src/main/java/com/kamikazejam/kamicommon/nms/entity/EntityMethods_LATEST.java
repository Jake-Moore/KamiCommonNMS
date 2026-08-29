package com.kamikazejam.kamicommon.nms.entity;

import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Working for: 1.21 CB, 1.21.8, 1.21.9
 *
 * <p><b>Its twin is {@code EntityMethods_1_21_11}, and the source is deliberately the same.</b>
 * That one is compiled against 1.21.11 and targets Java 21, because servers from 1.20.6 to 1.21.11
 * run on Java 21 and could not load a Java 25 class. This one targets Java 25, because 26.x
 * requires it. The ladder in {@code :core} sends everything up to 1.21.11 to the twin, and
 * everything above it here.</p>
 *
 * <p><b>Do not delete this copy in favour of the twin.</b> {@code v_latest} holds one of every
 * provider on purpose. It is the only module compiled against the newest Paper, so bumping {@code
 * highestPaperDep} compile-checks every provider against it. Dropping one from here gives up the
 * warning that a 26.x API change has broken it.</p>
 *
 * <p>Keep the two in step. If you change one, change the other.</p>
 */
@SuppressWarnings("unused")
public class EntityMethods_LATEST extends EntityMethodsPost_1_14 {

    @Override
    public void setPersists(@NotNull Creature creature, boolean value) {
        ((CraftCreature) creature).getHandle().persist = value;
    }

    @Override
    public void setFromSpawner(@NotNull Entity entity, boolean value) {
        ((CraftEntity) entity).getHandle().spawnedViaMobSpawner = value;
    }

    @Override
    public void setMobAI(@NotNull Entity entity, boolean value) {
        if (((CraftLivingEntity) entity).getHandle() instanceof Mob mob) {
            mob.aware = value;
        }
        ((CraftLivingEntity) entity).setAI(value);
    }
}
