package xyz.nikgub.zweihander.common.registries;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.nikgub.zweihander.Zweihander;

@SuppressWarnings("unused")
public class EntityTypeRegistry {
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Zweihander.MOD_ID);

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String registry_name, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITIES.register(registry_name, () -> entityTypeBuilder.build(registry_name));
    }
}
