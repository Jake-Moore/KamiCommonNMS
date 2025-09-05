package com.kamikazejam.kamicommon.nms.abstraction.entity;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Pre-1.13 implementation of {@link AbstractEntityMethods} that handles
 * spawner item manipulation using NBT data access.
 * <p>
 * This class provides spawner item handling for Minecraft versions prior to 1.13,
 * where spawner data was stored differently and required direct NBT manipulation.
 * It includes legacy entity type mapping to handle the transition between
 * old NMS names and modern {@link EntityType} values.
 * </p>
 * <p>
 * The implementation uses the NBT-API library to access and modify item NBT data,
 * and includes comprehensive entity type translation for legacy compatibility.
 * </p>
 */
@SuppressWarnings("unused")
public abstract class EntityMethodsPre_1_13 extends AbstractEntityMethods {

    /**
     * {@inheritDoc}
     * <p>
     * This implementation uses NBT data access to retrieve the spawner type
     * from the item's {@code BlockEntityTag} compound or legacy {@code type} tag.
     * </p>
     */
    @Override
    public @NotNull Optional<EntityType> getSpawnerType(@Nullable ItemStack stack) {
        return this.getSpawnerTypeByNBT(stack);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation uses NBT data modification to set the spawner type
     * in the item's {@code BlockEntityTag} compound using the legacy entity naming.
     * </p>
     */
    @Override
    public @NotNull ItemStack setSpawnerType(@NotNull ItemStack stack, @NotNull EntityType type) {
        return this.setSpawnerTypeByNBT(stack, type);
    }

    /**
     * Retrieves the spawner type from an {@link ItemStack} using NBT data access.
     * <p>
     * This method examines the item's NBT data to determine what entity type
     * the spawner will produce. It checks both the {@code BlockEntityTag.EntityId}
     * path (standard) and the legacy {@code type} tag for backwards compatibility.
     * </p>
     * <p>
     * The method includes validation to ensure the item is actually a spawner
     * and contains valid entity data before attempting translation.
     * </p>
     *
     * @param stack the {@link ItemStack} to examine, may be {@code null}
     * @return an {@link Optional} containing the {@link EntityType} if valid,
     *         empty if the stack is invalid or not a spawner
     */
    @NotNull
    protected Optional<EntityType> getSpawnerTypeByNBT(@Nullable ItemStack stack) {
        // Handle invalid stacks
        if (stack == null || !stack.hasItemMeta()) { return Optional.empty(); }
        // Or stacks that aren't a spawner
        XMaterial xMaterial = XMaterial.matchXMaterial(stack);
        if (xMaterial != XMaterial.SPAWNER) { return Optional.empty(); }

        ReadableNBT nbt = NBT.readNbt(stack);
        ReadableNBT nbtCompound = nbt.getCompound("BlockEntityTag");
        if (nbtCompound == null) {
            if (nbt.hasTag("type")) {
                return Optional.ofNullable(translateNMSTypes(nbt.getString("type").toUpperCase()));
            }
            return Optional.empty();
        }

        if (nbtCompound.getString("EntityId") == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(translateNMSTypes(nbtCompound.getString("EntityId").toUpperCase()));
    }

    @Nullable
    @SuppressWarnings("SpellCheckingInspection")
    private EntityType translateNMSTypes(@Nullable String nmsType) {
        if (nmsType == null) { return null; }

        EntityType type = switch (nmsType.toLowerCase()) {
            case "pigzombie" -> EntityType.PIG_ZOMBIE;
            case "complexpart" -> EntityType.COMPLEX_PART;
            case "cavespider" -> EntityType.CAVE_SPIDER;
            case "irongolem", "villagergolem" -> EntityType.IRON_GOLEM;
            case "magmacube", "lavaslime" -> EntityType.MAGMA_CUBE;
            case "mushroomcow" -> EntityType.MUSHROOM_COW;
            case "ozelot" -> EntityType.OCELOT;
            case "entityhorse" -> EntityType.HORSE;
            default -> {
                try {
                    yield EntityType.valueOf(nmsType.toUpperCase());
                } catch (final IllegalArgumentException exc) {
                    yield null;
                }
            }
        };
        if (type != null) { return type; }

        // Try XEntityType
        Optional<XEntityType> o = XEntityType.of(nmsType.toUpperCase());
        return o.map(XEntityType::get).orElse(null);

        // TODO extract more entity types from NMS and add to the switch statement
    }

    /**
     * Sets the spawner type in an {@link ItemStack} using NBT data modification.
     * <p>
     * This method modifies the item's NBT data to set what entity type the
     * spawner will produce when placed. It creates the necessary {@code BlockEntityTag}
     * compound and sets the {@code EntityId} using the legacy entity naming scheme.
     * </p>
     *
     * @param stack the spawner {@link ItemStack} to modify
     * @param type the {@link EntityType} the spawner should produce
     * @return the modified {@link ItemStack}, or {@code null} if the stack is invalid
     */
    @SuppressWarnings("deprecation")
    protected ItemStack setSpawnerTypeByNBT(ItemStack stack, @NotNull EntityType type) {
        if (stack == null || XMaterial.matchXMaterial(stack.getType()) != XMaterial.SPAWNER) { return null; }

        NBT.modify(stack, (nbt) -> {
            ReadWriteNBT compound = nbt.getOrCreateCompound("BlockEntityTag");
            compound.setString("EntityId", type.getName().toLowerCase());
        });
        return stack;
    }
}
